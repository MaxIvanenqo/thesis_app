package wwsis.ivanenqo.server;

import wwsis.ivanenqo.Commands;
import wwsis.ivanenqo.server.models.EncryptedMessage;
import wwsis.ivanenqo.server.models.FetchedCertificate;
import wwsis.ivanenqo.server.models.User;
import wwsis.ivanenqo.server.models.UserShort;
import wwsis.ivanenqo.server.utils.*;
import wwsis.ivanenqo.server.utils.configs.TCPConnectInterface;
import wwsis.ivanenqo.server.utils.configs.TCPConnection;
import wwsis.ivanenqo.server.utils.crypto.RSA;
import wwsis.ivanenqo.server.utils.crypto.RandomCodeGen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static wwsis.ivanenqo.Commands.*;

public class Server implements TCPConnectInterface, Runnable {
    private boolean                         stateFlag   = false;
    private final HashSet<TCPConnection>    connections = new HashSet<>();

    Server() {
        this.run();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            String command = reader.readLine();
            if (command == "stop"){
                ServerRun.stop();
            }
            if (command == "run"){
                ServerRun.run();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stop(){
        this.stateFlag = false;
        this.connections.forEach(el-> killConnection(el));
    }

    @Override
    public void run() {
        this.stateFlag = true;
        try {
            int PORT = 8888;
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (this.isAlive()){
                Socket socket = serverSocket.accept();
                if (socket != null){
                    new TCPConnection(this, socket);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void killConnection(TCPConnection connection){
        this.connections.remove(connection);
        try {
            connection.getSocket().close();
            connection.getThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void conversationInit(TCPConnection conn, byte[] id) {
        this.resetMessagesReadState(id, conn);
        String targetId = new String(id);
        if (conn.getUser().getActualConverserId() != null){
            if (conn.getUser().getActualConverserId().equals(targetId)) return;
        }
        conn.getUser().getArrOfDeliveredMsg().forEach(each->DBConnection.markDelivered(each.toString(), 0));
        conn.getUser().clearDeliveredMsgArray();
        conn.getUser().setActualConverserId(targetId);
        this.sendTargetCertificates(
                Objects.requireNonNull(
                        DBConnection.fetchAllTargetCertificates(targetId, conn.getUser().getIdDB())
                ),
                conn);
    }

    @Override
    public synchronized void logEnter(TCPConnection conn, byte[] req) {
        User user = new User();
        ArrayKit.pointToStart();
        byte[] device_id = ArrayKit.pullNextPartFromArray(req);
        byte[] email = ArrayKit.pullNextPartFromArray(req);
        String systemInfo = new String(ArrayKit.pullNextPartFromArray(req));
        user.setEmail(new String(email));
        user.setIdentificator(Objects.requireNonNull(User.findIdByEmail(new String(email)))[0]);
        user.setUuid(new String(device_id).substring(4));
        FetchedCertificate certificate = DBConnection.fetchOwnCertificate(user);
        if (certificate != null){
            user.setCertificate(certificate);
            user.setLoggingToken(RandomCodeGen.generate(8).getBytes());
            byte[] codeCheckEncrypted = RSA.encrypt(
                    user.getLoggingToken(),
                    HexArray.hexStringToBytes(user.getCertificate().getKey())
            );
            assert codeCheckEncrypted != null;
            byte[] codeCheckEncryptedSignedWithServerKey = RSA.encrypt(user.getLoggingToken());
            String systemInfoQuery = "SELECT device_name FROM certificates WHERE access_code='"+user.getUuid()+"'";
            try (Connection connection = DBConnection.getConnection();
                 Statement statement = Objects.requireNonNull(connection).createStatement();
                 ResultSet rs = statement.executeQuery(systemInfoQuery)) {
                rs.next();
                String fetched = rs.getString("device_name");
                if (!systemInfo.equals(fetched)) this.rejectLogging(conn);
                assert codeCheckEncryptedSignedWithServerKey != null;
                byte[] bytes = ArrayKit.joinArrays(
                        IntByteArray.convertToBytes(codeCheckEncrypted.length),
                        codeCheckEncrypted,
                        IntByteArray.convertToBytes(codeCheckEncryptedSignedWithServerKey.length),
                        codeCheckEncryptedSignedWithServerKey
                );
                conn.buildResponse(bytes, LOG_ENTER);
                conn.setUser(user);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else this.rejectLogging(conn);
    }

    private synchronized void fillUserInfo(TCPConnection conn) {
        DBConnection.fillUserInfo(conn.getUser());
    }

    public synchronized void rejectLogging(TCPConnection connection){
        connection.buildResponse(new byte[0], LOG_ERROR);
    }

    @Override
    public void signingUp(TCPConnection connection, byte[] req) {
        connection.setUser(new User(req));
        this.sendPinToEmail(connection);
    }

    private synchronized void sendPinToEmail(TCPConnection connection){
        connection.getUser().setSigningToken(RandomCodeGen.generate(6));
        SendMail.sendMessage(connection.getUser().getEmail(), connection.getUser().getSigningToken());
    }

    @Override
    public void groupUsers(TCPConnection connection) {
        User loggedUser = connection.getUser();
        HashSet<User> users = this.getContacts(loggedUser);
        if (users==null) users = new HashSet<>();
        var ref = new Object() {
            byte[] grouped = new byte[0];
        };
        byte[] length = IntByteArray.convertToBytes(users.size());
        ref.grouped = ArrayKit.joinArrays(ref.grouped, IntByteArray.convertToBytes(length.length), length);
        users.forEach((user) -> ref.grouped = ArrayKit.joinArrays(
                ref.grouped,
                user.toBytes()
        ));
        connection.buildResponse(ref.grouped, LOAD_USERS_RES);
    }

    @Override
    public void logging(byte[] req, TCPConnection connection) {
        this.fillUserInfo(connection);
        if (Arrays.equals(req, connection.getUser().getLoggingToken())){
            DBConnection.markOnline(connection.getUser().getEmail());
            setLogStatus(connection, LOGGED);
            DBConnection.updTimeCertificateUsed(connection.getUser().getUuid());
            this.connections.add(connection);
        }
        else setLogStatus(connection, LOG_ERROR);
    }

    @Override
    public void checkToken(TCPConnection connection, byte[] req) {
        String tokenRestored = new String(req, StandardCharsets.UTF_8);
        if (tokenRestored.equals(connection.getUser().getSigningToken())){
            this.saveUserToDB(connection);
            this.signUpSuccess(connection);
            return;
        }
        this.checkTokenError(connection);
    }

    @Override
    public void connectionEstablished(TCPConnection connection) {

    }

    @Override
    public synchronized void editPersonalData(TCPConnection connection, byte[] req) {
        String requestUpdate = new String(req);
        String line = requestUpdate.split(":line/value:")[0];
        String value = requestUpdate.split(":line/value:")[1];
        if (line.equals("email_address")) return;
        String query = "UPDATE personal_data SET "+line+"='"+value+"' WHERE email_address='"+connection.getUser().getEmail()+"'";
        DBConnection.updateQuery(query);
        connection.getUser().setLine(line, value);
        connection.buildResponse(connection.getUser().toBytes(), EDIT_PERSONAL_DATA_SUCCEEDED);
    }

    @Override
    public void findProfile(byte[] req, TCPConnection connection) {
        String search = new String(req);
        HashSet<UserShort> hs = new HashSet<>();
//      auto uzupełnianie polega na tym, że system szuka w tablicy "personal_data"
//      wskazaną przez użytkownika wartość. Szuka spośrod pól tablicy: email, full_name, phone_number
        hs.addAll(User.tryByEmail(search, connection));
        hs.addAll(User.tryByName(search, connection));
        hs.addAll(User.tryByPhone(search, connection));
//      Wszystkie wyniki poszukiwań grupujemy w HashSet. Gwarantujemy brak dublikatów
//      HashSet przetwarzamy w tablicę bajtów
//      Wykorzystanie obiektu anonimowego pomoże zmieniać wartość tablicy (pola obiektu)
//      w wyrażeniu lambda
        var obj = new Object() {
            byte[] profiles = new byte[0];
        };
        int size_ = hs.size();
        byte[] size = IntByteArray.convertToBytes(size_);
        obj.profiles = ArrayKit.joinArrays(obj.profiles, IntByteArray.convertToBytes(size.length), size);
        hs.forEach(userShort -> obj.profiles = ArrayKit.joinArrays(
                                                obj.profiles,
                                                userShort.toBytes()
                                                ));
        connection.buildResponse(obj.profiles, Commands.PROFILE_FIND_TIP);
    }

    @Override
    public void findProfileById(byte[] req, TCPConnection connection) {
        User user = User.findAllUserInfoByIdentificator(new String(req), connection);
        if (user != null)
            connection.buildResponse(user.toBytes(), FIND_PROFILE_BY_ID);
    }

    @Override
    public void addContact(byte[] req, TCPConnection connection) {
        ArrayKit.pointToStart();
        String idNewContact = new String(ArrayKit.pullNextPartFromArray(req));
        byte[] identificatorNewContact = ArrayKit.pullNextPartFromArray(req);
        connection.getUser().updOrSetContactList(idNewContact, connection);
        this.findProfileById(identificatorNewContact, connection);
    }

    @Override
    public void loadDialog(TCPConnection connection, byte[] req) {

    }

    @Override
    public void loadDeviceList(TCPConnection connection) {
        ArrayList<FetchedCertificate> arr = DBConnection.loadUserDevices(connection.getUser().getIdDB());
        var ref = new Object(){
            byte[] group = new byte[0];
        };
        assert arr != null;
        byte[] length = IntByteArray.convertToBytes(arr.size());

        ref.group = ArrayKit.joinArrays(ref.group, IntByteArray.convertToBytes(length.length), length);
        arr.forEach(certificate->{
            byte[] pubKey = certificate.getKey().getBytes();
            byte[] device_info = certificate.getDeviceInfo().getBytes();
            byte[] id = certificate.getId().getBytes();
            byte[] blocked = certificate.getBlocked().getBytes();
            byte[] last_use = certificate.getLastUse().getBytes();

            byte[] g = ArrayKit.joinArraysAutoSizeAdding(id, device_info, pubKey, blocked, last_use);
            ref.group = ArrayKit.joinArrays(
                    ref.group,
                    g);
        });
        connection.buildResponse(ref.group, LOAD_DEVICES_LIST_RES);
    }

    @Override
    public void changeCertificateStatus(byte[] req, TCPConnection conn) {
        String id = new String(req);
        String getActualStatusQuery = "SELECT blocked FROM certificates WHERE id='"+id+"'";
        String actualStatus = null;
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(getActualStatusQuery)
        ) {
            rs.next();
            actualStatus = rs.getString("blocked");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (actualStatus == null) return;

        int sa = Math.abs(Integer.parseInt(actualStatus)-1);
        String statusNew = String.valueOf(sa);
        String SQL = "UPDATE certificates SET blocked='"+statusNew+"' WHERE id='"+id+"'";
        DBConnection.updateQuery(SQL);
        if (id.equals(conn.getUser().getCertificate().getId())){
            conn.buildResponse(new byte[0], FORCED_LOGOUT);
        }
        else {
            loadDeviceList(conn);
        }
    }

    @Override
    public void listOfPossibleContactChoices(TCPConnection conn) {
        String query =
                "SELECT mydb.users.is_online, " +
                        "mydb.users.identificator, " +
                        "mydb.users.id, " +
                        "mydb.users.last_seen, " +
                        "mydb.personal_data.full_name, " +
                        "mydb.personal_data.user_photo, " +
                        "mydb.personal_data.email_address, " +
                        "mydb.personal_data.description, " +
                        "mydb.personal_data.phone_number " +
                "FROM mydb.users " +
                "LEFT JOIN " +
                        "mydb.personal_data " +
                        "ON mydb.users.id = mydb.personal_data.users_id " +
                    "WHERE users.id=" +
                        "(SELECT users_id FROM mydb.certificates WHERE id=" +
                        "(SELECT certificates_sender_id FROM mydb.messages " +
                            "WHERE certificates_address_id='"+conn.getUser().getCertificate().getId()+"'" +
                        "AND users_id<>'"+conn.getUser().getIdDB()+"' LIMIT 1))";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(query)
        ) {
            HashSet<User> list = new HashSet<>();
            while (rs.next()){
                list.add(new User(
                        rs.getString("is_online"),
                        rs.getString("id"),
                        rs.getString("identificator"),
                        rs.getString("full_name"),
                        rs.getString("user_photo"),
                        rs.getString("email_address"),
                        rs.getString("description"),
                        rs.getString("phone_number"),
                        rs.getString("last_seen"),
                        true));
            }
            var ref = new Object(){
                byte[] group = new byte[0];
            };
            HashSet<User> listContacts = this.getContacts(conn.getUser());
            if (listContacts != null) list.addAll(listContacts);
            byte[] length = IntByteArray.convertToBytes(list.size());
            ArrayList<String> stringArrayList = new ArrayList<>();
            String query1 = "SELECT sender_id FROM messages WHERE timestamp>(SELECT last_seen FROM users WHERE id='"+conn.getUser().getIdDB()+"')";
            try (Connection connection1 = DBConnection.getConnection();
            Statement statement1 = connection1.createStatement();
            ResultSet rs1 = statement1.executeQuery(query1)) {
                while (rs1.next()){
                    String senderID = rs1.getString("sender_id");
                    if (senderID!=null)
                        stringArrayList.add(senderID);
                }

            }
            list.forEach((el)->{
                if (stringArrayList.contains(el.getIdDB())){
                    el.setGotNewMsg();
                }
            });

            ref.group = ArrayKit.joinArrays(ref.group, IntByteArray.convertToBytes(length.length), length);
            list.forEach(el-> ref.group = ArrayKit
                    .joinArrays(
                            ref.group,
                            el.toBytes()));
            conn.buildResponse(ref.group, LIST_OF_POSSIBLE_CHOICES_RES);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void burnMessages(TCPConnection connection, byte[] req) {
        String senderId = new String(req);
        String query = "DELETE FROM messages " +
                "WHERE address_id='"+connection.getUser().getIdDB()+"' AND sender_id='"+senderId+"'";
        DBConnection.updateQuery(query);
        query = "DELETE FROM messages " +
                "WHERE address_id='"+senderId+"' AND sender_id='"+connection.getUser().getIdDB()+"'";
        DBConnection.updateQuery(query);
        connection.buildResponse(req, BURN_MESSAGES_DONE);
    }

    @Override
    public void logout(TCPConnection connection) {
        DBConnection.updTimeCertificateUsed(connection.getUser().getUuid());
        DBConnection.markOffline(connection.getUser().getEmail());
        String QUERY = "UPDATE messages SET delivered='0' WHERE certificates_address_id='"+connection.getUser().getCertificate().getId()+"'";
        DBConnection.updateQuery(QUERY);
        this.killConnection(connection);
    }

    @Override
    public void loadAttachment(byte[] req, TCPConnection connection) {
        String QUERY = "SELECT messages.attachment, " +
                        "certificates.key " +
                            "FROM messages " +
                            "LEFT JOIN certificates " +
                            "ON messages.certificates_sender_id=certificates.id " +
                       "WHERE messages.id='"+new String(req)+"'";
        try (Connection conn = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(conn).createStatement();
             ResultSet rs = statement.executeQuery(QUERY)){
        rs.next();
        String attachment = rs.getString("attachment");
        String key = rs.getString("key");
        byte[] bytes = ArrayKit.joinArrays(
                IntByteArray.convertToBytes(attachment.getBytes().length),
                attachment.getBytes(),
                IntByteArray.convertToBytes(key.getBytes().length),
                key.getBytes()
        );
        connection.buildResponse(bytes, LOAD_ATTACHMENT_RES);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void resetMessagesReadState(byte[] req, TCPConnection connection) {
        String id = new String(req);
        String QUERY = "UPDATE messages SET delivered='0' WHERE" +
                " certificates_address_id='"+connection.getUser().getCertificate().getId()+"' AND" +
                " sender_id='"+id+"'";
        DBConnection.updateQuery(QUERY);
        QUERY = "UPDATE messages SET delivered='0' WHERE" +
                " certificates_address_id='"+connection.getUser().getCertificate().getId()+"' AND" +
                " address_id='"+connection.getUser().getIdDB()+"'";
        DBConnection.updateQuery(QUERY);
    }

    @Override
    public void removeContact(byte[] req, TCPConnection connection) {
        ArrayKit.pointToStart();
        byte[] id = ArrayKit.pullNextPartFromArray(req);
        byte[] identificator = ArrayKit.pullNextPartFromArray(req);
        ArrayList<String> contacts = User.fetchContactList(connection.getUser().getIdDB());
        String contactToRemove = new String(id);
        ArrayList<String> newContacts = new ArrayList<>();
        for (String el : contacts){
            boolean equals = el.trim().equals(contactToRemove.trim());
            if (equals) continue;
            newContacts.add(el);
        }
        String QUERY = "UPDATE users SET contacts='"+newContacts+"' WHERE id='"+connection.getUser().getIdDB()+"'";
        System.out.println(contactToRemove + " " + QUERY);
        DBConnection.updateQuery(QUERY);
        this.getContacts(connection.getUser());
        this.findProfileById(identificator, connection);

    }

    private synchronized void saveUserToDB(TCPConnection connection){
        connection.getUser().addUserToDB();
    }

    private void signUpSuccess(TCPConnection connection){
        connection.buildResponse(connection.getUser().getUuid().getBytes(), SIGN_SUCCESS);
    }

    private void checkTokenError(TCPConnection connection){
        connection.buildResponse(new byte[0], SIGNING_ERROR);
    }

    private void setLogStatus(TCPConnection connection, int logstatus){
        connection.getUser().setIdentificator(Objects.requireNonNull(User.findIdByEmail(connection.getUser().getEmail()))[0]);
        connection.getUser().setIdDB(Objects.requireNonNull(User.findIdByEmail(connection.getUser().getEmail()))[1]);
        byte[] user = connection.getUser().toBytes();
        connection.buildResponse(user, logstatus);
    }

    private synchronized HashSet<User> getContacts(User user){
        return DBConnection.getContacts(user);
    }

    private void sendTargetCertificates(ArrayList<FetchedCertificate> arr, TCPConnection connection) {
//      Odpowiedź z certyfikatami kluczy publicznych uzytkownika. Każdy użytkownik ma tyle certyfikatów ile
//      ma zarejestrowanych urządzeń
        var ref = new Object(){
            byte[] group = new byte[0];
        };
//      Info dla interpretacji na stronie klienta.
//      Tak klient wie ile certyfikatów musi wczytać (wczytuje w pętli while (while(length!=0){... ;length--}))
        byte[] length = IntByteArray.convertToBytes(arr.size());//arr.size() - ilość certyfikatów uż.

//      Łączymy tablicę (na razie pustą, rozmiar się zmieni automatycznie (ArrayKit)), którą wyślemy
//      użytkownikowi z informacją o ilości certyfikatów. Info w postaci bajtów 4 bajty - int, wskazuje
//      ile następnych bajtów zajmuje informacja o rozmiarze (tak naprawde... to int... zawsze 4 bajty)
        ref.group = ArrayKit.joinArrays(ref.group, IntByteArray.convertToBytes(length.length), length);
        arr.forEach(certificate->{
            byte[] pubKey = certificate.getKey().getBytes();
            byte[] id = certificate.getId().getBytes();
            byte[] g = ArrayKit.joinArraysAutoSizeAdding(id, pubKey);
            ref.group = ArrayKit.joinArrays(
                    ref.group,
                    g);
        });
        connection.buildResponse(ref.group, CONVERSATION_INIT);
        this.selectMessages(connection);
    }

    private synchronized void selectMessages(TCPConnection conn){
        ArrayList<EncryptedMessage> arr = DBConnection.selectMessages(conn);
        if (arr != null && !arr.isEmpty()){
            arr.forEach(msg-> {
                byte[] m = msg.toBytes();
                conn.buildResponse(m, SEND_MESSAGE);

            });
        }

    }

    @Override
    public synchronized void onMessageGot(TCPConnection connection, final byte[] msg) {
        this.insertMsgToDB(connection, msg);
    }

    private synchronized void insertMsgToDB(final TCPConnection conn, final byte[] msg){
        EncryptedMessage encryptedMessage = new EncryptedMessage(msg);
        DBConnection.insertMsgToDB(conn.getUser(), encryptedMessage);
        this.selectMessages(conn);
        this.sendImmediately(encryptedMessage.getCertTargetId());
    }

    private synchronized void sendImmediately(String certId){
        if (User.isOnline(certId)) {
            this.connections.forEach(conn->{
                User tmp = new User();
                tmp.setEmail(conn.getUser().getEmail());
                if (conn.getUser().equals(tmp)){
                    this.selectMessages(conn);
                }
            });
        }
    }

    public boolean isAlive(){
        return this.stateFlag;
    }


}
