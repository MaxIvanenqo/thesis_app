package wwsis.ivanenqo.server.utils.configs;

public interface TCPConnectInterface {
    void onMessageGot(TCPConnection connection, byte[] b);

    void killConnection(TCPConnection connection);

    void conversationInit(TCPConnection connection, byte[] b);

    void logEnter(TCPConnection connection, byte[] req);

    void signingUp(TCPConnection connection, byte[] req);

    void groupUsers(TCPConnection connection);

    void logging(byte[] req, TCPConnection connection);

    void checkToken(TCPConnection connection, byte[] req);

    void connectionEstablished(TCPConnection connection);

    void editPersonalData(TCPConnection connection, byte[] req);

    void findProfile(byte[] req, TCPConnection connection);

    void findProfileById(byte[] req, TCPConnection connection);

    void addContact(byte[] req, TCPConnection connection);

    void loadDialog(TCPConnection connection, byte[] req);

    void loadDeviceList(TCPConnection connection);

    void changeCertificateStatus(byte[] req, TCPConnection connection);

    void listOfPossibleContactChoices(TCPConnection tcpConnection);

    void burnMessages(TCPConnection connection, byte[] req);

    void logout(TCPConnection connection);

    void loadAttachment(byte[] req, TCPConnection connection);

    void resetMessagesReadState(byte[] req, TCPConnection connection);

    void removeContact(byte[] req, TCPConnection connection);
}
