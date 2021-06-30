package wwsis.ivanenqo.server.models;

import wwsis.ivanenqo.server.utils.ArrayKit;
import wwsis.ivanenqo.server.utils.DBConnection;
import wwsis.ivanenqo.server.utils.IntByteArray;
import wwsis.ivanenqo.server.utils.UserImage;
import wwsis.ivanenqo.server.utils.configs.TCPConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class User {

    private String uuid;

    private String email;

    private String pubKey;

    private String fullName;

    private String phoneNumber;

    private String description;

    private String identificator; // unikalne imię użytkownika. generowane automatycznie

    private String userPhoto; // na etapie rejestracji generowane automatycznie

    private String signingToken; // na email przy rejestracji

    private byte[] loggingToken; // do szyfrowania kluczem pub. użytkonika przy logowaniu

    private String idDB;

    private FetchedCertificate certificate; // certyfikat klucza pub. aktywnego w połączeniu urządzenia

    private HashSet<String> hashSet; // potrzebne jest dla generowania umikalnej wartości 'identificator'

    private String actualConverserId;

    private String certificateOwner;

    private HashSet<String> hashSetUUID;

    private ArrayList<Integer> arrOfDeliveredMsg;

    private String gotNewMsg;

    private boolean isExist;

    public String getGotNewMsg() {
        return gotNewMsg==null?"0":this.gotNewMsg;
    }

    public void setGotNewMsg() {
        this.gotNewMsg = "1";
    }

    private String userNetStatus;

    @Override
    public String toString(){
        String email = this.email==null?"email":this.email;
        String fullName = this.fullName==null?"fullname":this.fullName;
        return email + " " + fullName;
    }

    private String last_seen;
    private String isContact;

    public String getLastSeen() {
        return last_seen==null?"?":last_seen;
    }

    public User(){}

    public User(String is_online, String id, String identificator, String fullName, String userPhoto, String email, String description, String phoneNumber, String lastSeen, boolean contact){
        this.email = email;
        this.userNetStatus = is_online;
        this.idDB = id;
        this.identificator = identificator;
        this.userPhoto = userPhoto;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.hashSetUUID = new HashSet<>();
        this.gotNewMsg = "0";
        this.last_seen = lastSeen;
        this.isContact = contact?"1":"0";
    }

    public User(byte[] req) {
       ArrayKit.pointToStart();
        byte[] certificateOwner = ArrayKit.pullNextPartFromArray(req);
        byte[] email = ArrayKit.pullNextPartFromArray(req);
        byte[] fullName = ArrayKit.pullNextPartFromArray(req);
        byte[] pubKey = ArrayKit.pullNextPartFromArray(req);
        this.email = new String(email);
        this.certificateOwner = new String(certificateOwner);
        this.fullName = new String(fullName);
        this.pubKey = new String(pubKey);
        System.out.println(this.email+" " + this.certificateOwner + " " + this.fullName + " " + this.pubKey);
    }

    public static synchronized boolean isOnline(String certId){
        String SQL = "SELECT is_online FROM users WHERE id=(SELECT users_id FROM certificates WHERE id='"+certId+"')";
        System.out.println(SQL);
        try(Connection connection = DBConnection.getConnection();
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet rs = statement.executeQuery(SQL)){
            rs.next();
            return rs.getString("is_online").equals("1");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void addArrOfDeliveredMsg(int n) {
        if (this.arrOfDeliveredMsg == null) this.arrOfDeliveredMsg = new ArrayList<>();
        this.arrOfDeliveredMsg.add(n);
    }

    public ArrayList<Integer> getArrOfDeliveredMsg() {
        if (this.arrOfDeliveredMsg == null) this.arrOfDeliveredMsg = new ArrayList<>();
        return arrOfDeliveredMsg;
    }

    public void clearDeliveredMsgArray(){
        this.arrOfDeliveredMsg = new ArrayList<>();
    }

    public void setActualConverserId(String actualConverserId) {
        this.actualConverserId = actualConverserId;
    }

    public String getActualConverserId() {
        return actualConverserId;
    }

    public void setLine(String line, String value){
//      jedna metoda na wszystkie możliwe zapytania o zmianę danych osobowych w BD
        switch (line){
            case "description":
                this.setDescription(value);
                break;
            case "phone_number":
                this.setPhoneNumber(value);
                break;
            case "user_photo":
                this.setUserPhoto(value);
                break;
            default: break;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description==null?"":description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return (phoneNumber==null)? "no number": phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) phoneNumber = "no number";
        this.phoneNumber = phoneNumber;
    }

    public FetchedCertificate getCertificate() {
        return certificate;
    }

    public void setCertificate(FetchedCertificate certificate) {
        this.certificate = certificate;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName==null?"":fullName;
    }

    public String getIdDB() {
        return idDB==null?"0":idDB;
    }

    public void setIdDB(String idDB) {
//      nie unikalny identyfikator. id wpisu w tablicy users
        this.idDB = idDB;
    }

    public byte[] getLoggingToken() {
        return loggingToken;
    }

    public void setLoggingToken(byte[] loggingToken) {
        this.loggingToken = loggingToken;
    }

    public String getSigningToken() {
        return signingToken;
    }

    public void setSigningToken(String signingToken) {
        this.signingToken = signingToken;
    }

    public void setUserPhoto(String ph) {
//      Zdjęcie -> byte[] -> HEX String
        this.userPhoto = ph;
    }

    public synchronized void addUserToDB(){
        this.userPhoto = UserImage.draw(this.fullName);
        hashSet = new HashSet<>();
        this.uuid = UUID.randomUUID().toString();
        if (!this.isExist()){
            this.userInsert();
            this.personalDataInsert();
        }
        this.insertCertificate();
    }

    public synchronized boolean isExist(){
        String QUERY = "SELECT id FROM personal_data WHERE email_address='"+this.email+"'";
        try (Connection connection = DBConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(QUERY)) {
            return rs.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void setIdentificator(String id){
        this.identificator = id;
    }

    private void idGenerator(String name){
        if (!this.hashSet.add(name)) idGenerator(name+"_"+this.hashSet.size());
        else this.identificator = name;
    }

    public void uuidGenerator(String uuid){
        if (this.hashSetUUID == null) this.hashSetUUID = new HashSet<>();
        if (!this.hashSetUUID.add(uuid)) idGenerator(uuid+"_"+this.hashSet.size());
        else this.uuid = uuid;
    }

    private synchronized void userInsert(){
        this.idGenerator(this.fullName.replace(" ", "_").toLowerCase());
        String query = "INSERT INTO users (identificator) VALUES ('"+this.identificator+"')";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement()){
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            userInsert();
        }
    }


    private synchronized void insertCertificate(){
        this.uuidGenerator(UUID.randomUUID().toString());
        String query = "INSERT INTO `certificates` (`users_id`, `access_code`, `key`, `device_name`) " +
                "VALUES ((SELECT `users_id` FROM `personal_data` WHERE `email_address`=" +
                "'"+this.email+"'" +
                "), '"+this.uuid+"', '"+this.pubKey+"', '"+this.certificateOwner+"')";
        DBConnection.updateQuery(query);
    }

    private synchronized void personalDataInsert(){
        String query = "INSERT INTO personal_data (email_address, full_name, users_id, user_photo) VALUES ('"+this.email+"', '"+this.fullName+"', (SELECT id FROM users WHERE identificator='"+this.identificator+"'), '"+this.userPhoto+"')";
        DBConnection.updateQuery(query);
    }

    public synchronized static String[] findIdByEmail(String val){
        String SQL = "SELECT * FROM `users` WHERE `id`=(SELECT users_id FROM `personal_data` WHERE email_address='"+val+"') ";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)){
                String identificator = null;
                String idDB = null;
                while (rs.next()){
                    idDB = rs.getString("id");
                    identificator = rs.getString("identificator");
                }
             return new String[]{identificator, idDB};
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }



    public synchronized static User findAllUserInfoByIdentificator(String id, TCPConnection conn){
        ArrayList<String> contacts = fetchContactList(conn.getUser().getIdDB());
        String SQL = "" +
                "SELECT " +
                "mydb.users.is_online, " +
                "mydb.users.identificator, " +
                "mydb.users.id, " +
                "mydb.users.last_seen, " +
                "mydb.personal_data.email_address," +
                "mydb.personal_data.full_name, " +
                "mydb.personal_data.description, " +
                "mydb.personal_data.user_photo, " +
                "mydb.personal_data.phone_number " +
                "FROM mydb.users " +
                "LEFT JOIN mydb.personal_data " +
                "ON mydb.users.id = mydb.personal_data.users_id " +
                "WHERE users.identificator='"+id+"'";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)){
            User user = null;
            while (rs.next()){
                String isContactStr = rs.getString("id").trim();
                System.out.println(contacts + " " + contacts.contains(isContactStr));
                boolean isContact = contacts.contains(isContactStr);
                user = new User(
                        rs.getString("is_online"),
                        rs.getString("id"),
                        id,
                        rs.getString("full_name"),
                        rs.getString("user_photo"),
                        rs.getString("email_address"),
                        rs.getString("description"),
                        rs.getString("phone_number"),
                        rs.getString("last_seen"),
                        isContact
                );
            }
            return user;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public synchronized static ArrayList<UserShort> tryByName(String name, TCPConnection connection){
        return findBy("full_name", name, connection);
    }

    public synchronized static ArrayList<UserShort> tryByEmail(String email, TCPConnection connection){
        return findBy("email_address", email, connection);
    }

    public synchronized static ArrayList<UserShort> tryByPhone(String phone, TCPConnection connection){
        return findBy("phone_number", phone, connection);
    }

    private synchronized static ArrayList<UserShort> findBy(String line, String val, TCPConnection conn){
        String SQL = "SELECT * FROM `personal_data` WHERE `" + line + "` LIKE '%"+val+"%'";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)){
                ArrayList<UserShort> arrayList = new ArrayList<>();
                while (rs.next()){
                    String email=rs.getString("email_address");
                    String phone=rs.getString("phone_number");
                    String full_name=rs.getString("full_name");
                    if (!email.equals(conn.getUser().getEmail()))
                        arrayList.add(
                                new UserShort(
                                    Objects.requireNonNull(findIdByEmail(email))[0],
                                    Objects.requireNonNull(findIdByEmail(email))[1],
                                    email,
                                    full_name,
                                    phone));
                }
            return arrayList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public synchronized static ArrayList<String> fetchContactList(String id) {
        String query = "SELECT contacts FROM users WHERE id='"+id+"'";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(query)
        ) {
            rs.next();
            String fetchedString = rs.getString("contacts");
            if (fetchedString == null) return new ArrayList<>();
            String strWithoutBrackets = fetchedString.substring(1, fetchedString.length()-1);
            String[] strArr = strWithoutBrackets.split(",");
            ArrayList<String> arrayList = new ArrayList<>();
            for (String s : strArr) {
                if (s.equals("")) continue;
                arrayList.add(s.trim());
            }
            return arrayList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updOrSetContactList(String idNewContact, TCPConnection connection) {
        ArrayList<String> arrayList = fetchContactList(connection.getUser().getIdDB());
        if (arrayList.contains(idNewContact)) return;
        arrayList.add(idNewContact);
        System.out.println(idNewContact);
        String query = "UPDATE users SET contacts='"+arrayList.toString()+"'" +
                " WHERE id='"+this.idDB+"'";
        System.out.println(query);
        DBConnection.updateQuery(query);
    }

    public String getUserNetStatus() {
        if (userNetStatus == null) return "0";
        return userNetStatus;
    }

    public String getUserPhoto() {
        return userPhoto==null?"":userPhoto;
    }

    public byte[] toBytes(){
        return ArrayKit.joinArrays(
                IntByteArray.convertToBytes(this.getUserNetStatus().getBytes().length),
                this.getUserNetStatus().getBytes(),
                IntByteArray.convertToBytes(this.getIdDB().getBytes().length),
                this.getIdDB().getBytes(),
                IntByteArray.convertToBytes(this.getIdentificator().getBytes().length),
                this.getIdentificator().getBytes(),
                IntByteArray.convertToBytes(this.getFullName().getBytes().length),
                this.getFullName().getBytes(),
                IntByteArray.convertToBytes(this.getUserPhoto().getBytes().length),
                this.getUserPhoto().getBytes(),
                IntByteArray.convertToBytes(this.getEmail().getBytes().length),
                this.getEmail().getBytes(),
                IntByteArray.convertToBytes(this.getDescription().getBytes().length),
                this.getDescription().getBytes(),
                IntByteArray.convertToBytes(this.getPhoneNumber().getBytes().length),
                this.getPhoneNumber().getBytes(),
                IntByteArray.convertToBytes(this.getGotNewMsg().getBytes().length),
                this.getGotNewMsg().getBytes(),
                IntByteArray.convertToBytes(this.getLastSeen().getBytes().length),
                this.getLastSeen().getBytes(),
                IntByteArray.convertToBytes(this.getIsContact().getBytes().length),
                this.getIsContact().getBytes()
        );
    }

    public String getIsContact() {
        return isContact==null?"0":isContact;
    }

    public String getIdentificator() {
        return identificator==null? "":identificator;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
