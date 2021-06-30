package wwsis.ivanenqo.server.utils;

import wwsis.ivanenqo.server.models.EncryptedMessage;
import wwsis.ivanenqo.server.models.FetchedCertificate;
import wwsis.ivanenqo.server.models.User;
import wwsis.ivanenqo.server.utils.configs.TCPConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class DBConnection {
    public static Connection connection = null;

    public static Connection getConnection(){
        String password = "PASSWORD";
        String userName = "USERNAME";
        String url = "jdbc:mysql://" +
                "localhost" +
                ":3306/" +
                "mydb?" +
                "useUnicode=true&" +
                "characterEncoding=utf8&" +
                "useSSL=true&" +
                "useLegacyDatetimeCode=true&" +
                "serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection= DriverManager.getConnection(url, userName, password);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void updateQuery(String query){
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement()){
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static synchronized void markOnline(String email){
        String SQL = "UPDATE users SET is_online='1' WHERE id=(SELECT users_id FROM personal_data WHERE email_address='"+email+"')";
        DBConnection.updateQuery(SQL);
    }

    public static synchronized void markOffline(String email){
        String SQL = "UPDATE users SET is_online='0' WHERE id=(SELECT users_id FROM personal_data WHERE email_address='"+email+"')";
        DBConnection.updateQuery(SQL);
    }

    public static void markDelivered(String id, int option){
        String SQL = "UPDATE messages SET delivered='"+option+"' WHERE id='"+id+"'";
        DBConnection.updateQuery(SQL);
    }

    public static synchronized FetchedCertificate fetchOwnCertificate(User user){
        String SQL = "SELECT * FROM `certificates` WHERE users_id=" +
                "(SELECT `users_id` FROM `personal_data` WHERE `email_address`='"+user.getEmail()+"')";
        try{
            Connection connection = DBConnection.getConnection();
            assert connection != null;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SQL);
            while (rs.next()){
                if (rs.getString("blocked").equals("1")) {
                    continue;
                }
                String idActivatedCertificate = rs.getString("id");
                String access_code = rs.getString("access_code");
                String key = rs.getString("key");
                return new FetchedCertificate(idActivatedCertificate, access_code, key);
            }
        }
        catch (SQLException throwables) {
            return null;
        }
        return null;
    }

    public static synchronized ArrayList<FetchedCertificate> fetchAllTargetCertificates(String targetId, String ownId) {
        String SQL = "SELECT * FROM certificates WHERE blocked='0' AND users_id='" + targetId + "' OR users_id='" + ownId + "'";
        ArrayList<FetchedCertificate> arr = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)) {
            while (rs.next()) {
                arr.add(new FetchedCertificate(
                        rs.getString("id"),
                        rs.getString("access_code"),
                        rs.getString("key")));
            }
            return arr;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

        public static synchronized ArrayList<EncryptedMessage> selectMessages(TCPConnection conn){
        String SQL = "SELECT " +
                "mydb.messages.id, " +
                "mydb.messages.sender_id, " +
                "mydb.messages.address_id, " +
                "mydb.personal_data.email_address, " +
                "mydb.messages.certificates_sender_id, " +
                "mydb.messages.attachment, " +
                "mydb.messages.encrypted_message, " +
                "mydb.messages.protected_key, " +
                "mydb.messages.timestamp " +
                "FROM mydb.messages " +
            "LEFT JOIN mydb.personal_data " +
            "ON mydb.messages.address_id = mydb.personal_data.users_id " +
            "WHERE certificates_address_id = '"+conn.getUser().getCertificate().getId()+"' AND delivered=0";

        ArrayList<EncryptedMessage> arr = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)) {
            while (rs.next()){
                String id = rs.getString("id");
                String senderUserId = rs.getString("sender_id");
                String addressUserId = rs.getString("address_id");
                String certSenderId = rs.getString("certificates_sender_id");
                String senderPubKey = Objects.requireNonNull(DBConnection.fetchCertificateById(certSenderId)).getKey();
                String msg = rs.getString("encrypted_message");
                String timestamp = rs.getString("timestamp");
                String protected_key = rs.getString("protected_key");
                EncryptedMessage em = new EncryptedMessage(
                        id,
                        senderUserId,
                        addressUserId,
                        senderPubKey,
                        timestamp,
                        msg,
                        protected_key);
                arr.add(em);
                DBConnection.markDelivered(id, 1);
                conn.getUser().addArrOfDeliveredMsg(Integer.parseInt(id));
            }
            if (arr.size() == 0) return null;
            return arr;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static synchronized void insertMsgToDB(final User user, EncryptedMessage encryptedMessage){
        String query = "INSERT INTO messages (" +
                "encrypted_message, " +
                "protected_key, " +
                "attachment, " +
                "certificates_sender_id, " +
                "certificates_address_id, " +
                "timestamp, " +
                "delivered, " +
                "address_id, " +
                "sender_id) VALUES ('"+
                encryptedMessage.getMsg()+"', '"+
                encryptedMessage.getProtectedKey() +"', '" +
                encryptedMessage.getAttachment() +"', '" +
                user.getCertificate().getId()+"', '"+
                encryptedMessage.getCertTargetId()+"', "+
                "NOW(), 0, '" +
                encryptedMessage.getTargetUserId()+"', '"+
                user.getIdDB()+"')";
        DBConnection.updateQuery(query);
    }

    public static synchronized FetchedCertificate fetchCertificateById(String id){
        String SQL = "SELECT * FROM certificates WHERE id='"+id+"'";
        Connection connection = DBConnection.getConnection();
        if (connection == null) return null;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SQL);
            rs.next();
//          Certyfikat może być zablokowany gdy użytkownik blokuje jedno ze swoich urządzeń (lub wszystkie)
//          Użytkonik nie jest w stanie odczytać stare wiadomości z tego urządzenia.
//          Zablokowany certyfikat także oznacza że użytkownik nie będzie w stanie zalogować się z tego urządzenia
//            (dla logowania potrzebny jest klucz pub. z tego certifikatu)
            if (rs.getString("blocked").equals("1")) return null;
            String key = rs.getString("key");
            String access_code = rs.getString("access_code");
            return new FetchedCertificate(id, access_code, key);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public static synchronized void fillUserInfo(User user) {
        String SQL = "SELECT * FROM personal_data WHERE `email_address`='"+user.getEmail()+"'";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)) {
            while (rs.next()) {
                String fn = rs.getString("full_name");
                String pn = rs.getString("phone_number");
                String ds = rs.getString("description");
                String ph = rs.getString("user_photo");
                user.setFullName(fn);
                user.setPhoneNumber(pn);
                user.setDescription(ds);
                user.setUserPhoto(ph);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static synchronized User fetchEachContactsUser(String id){
        String query = "SELECT " +
                "mydb.users.is_online, " +
                "mydb.users.identificator, " +
                "mydb.users.id, " +
                "mydb.users.last_seen, " +
                "mydb.personal_data.email_address, " +
                "mydb.personal_data.full_name, " +
                "mydb.personal_data.description, " +
                "mydb.personal_data.user_photo, " +
                "mydb.personal_data.phone_number " +
                "FROM mydb.users " +
                "LEFT JOIN mydb.personal_data " +
                "ON mydb.users.id = mydb.personal_data.users_id " +
                "WHERE users.id='"+id+"'";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(query)){

            if (rs.next())

            return new User(
                    rs.getString("is_online"),
                    rs.getString("id"),
                    rs.getString("identificator"),
                    rs.getString("full_name"),
                    rs.getString("user_photo"),
                    rs.getString("email_address"),
                    rs.getString("description"),
                    rs.getString("phone_number"),
                    rs.getString("last_seen"),
                    true
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static synchronized HashSet<User> getContacts(User user){
        String contacts;
        String[] stringContacts = null;
        String fetchContactArrQuery = "SELECT contacts FROM mydb.users WHERE id = "+user.getIdDB()+"";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(fetchContactArrQuery)){
            rs.next();
            contacts = rs.getString("contacts");
            if (contacts == null) return null;
            String tmp = contacts.substring(1, contacts.length()-1);
            stringContacts = tmp.split(",");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        HashSet<User> users = new HashSet<>();

        if (stringContacts == null || stringContacts.length==0) return new HashSet<>();
        for (String id : stringContacts) {
            User fetched = DBConnection.fetchEachContactsUser(id);
            if (fetched != null) {
                users.add(fetched);
            }
        }
        return users;
    }

    public static void updTimeCertificateUsed(String id){
        System.out.println(id);
        String SQL = "UPDATE certificates SET last_use=NOW()  WHERE access_code='"+id+"'";
        DBConnection.updateQuery(SQL);
    }

    public static synchronized ArrayList<FetchedCertificate> loadUserDevices(String id){
        String SQL = "SELECT * FROM certificates WHERE users_id='"+id+"'";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = Objects.requireNonNull(connection).createStatement();
             ResultSet rs = statement.executeQuery(SQL)) {
            ArrayList<FetchedCertificate> arr = new ArrayList<>();
            while (rs.next()){
                String device_info = rs.getString("device_name");
                String idDB = rs.getString("id");
                String key = rs.getString("key");
                String lastuse = rs.getString("last_use");
                String blocked = rs.getString("blocked");
                arr.add(new FetchedCertificate(idDB, device_info, key, blocked, lastuse));
            }
            return arr;
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return null;
    }
}


//AAAAAA [0, 0, 30, -68, 48, 48, 48, 48, 48, 66, 69, 48, 48, 57, 52, 69, 65, 67, 49, 55, 52, 49, 48, 65, 49, 67, 51, 65, 55, 67, 69, 67, 49, 50, 70, 69, 48, 53, 49, 56, 68, 66, 54, 50, 50, 67, 68, 55, 50, 67, 69, 56, 67, 50, 70, 70, 55, 57, 68, 65, 67, 53, 65, 52, 70, 66, 70, 65, 51, 65, 48, 68, 53, 68, 51,
//AAAAAA 11 _____       [48, 48, 48, 48, 48, 66, 69, 48, 48, 57, 52, 69, 65, 67, 49, 55, 52, 49, 48, 65, 49, 67, 51, 65, 55, 67, 69, 67, 49, 50, 70, 69, 48, 53, 49, 56, 68, 66, 54, 50, 50, 67, 68, 55, 50, 67, 69, 56, 67, 50, 70, 70, 55, 57, 68, 65, 67, 53, 65, 52, 70, 66, 70, 65, 51, 65, 48, 68, 53, 68, 51, 56, 56, 50, 68,
