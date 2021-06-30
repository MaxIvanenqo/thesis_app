package client;

public interface TCPConnectInterface {
    void onDisconnect();

    void loggingStatus(byte[] res);

    void logging(byte[] res);

    void loadedUsers(byte[] res);

    void signUpSuccess(byte[] res);

    void fetchConverserCertificates(byte[] res);

    void parseNewMessage(byte[] res);

    void logAllow(byte[] res);

    void logError(byte[] res);

    void personalDataUpdated(byte[] res);

    void findProfileTip(byte[] res);

    void fillProfile(byte[] res);

    void fillDevicesList(byte[] res);

    void fetchListOfPossibleDialogs(byte[] res);

    void messagesBurn(byte[] res);

    void loadAttachmentRes(byte[] res);

    void forcedLogout();
}
