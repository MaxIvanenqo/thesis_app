package client;

import client.utils.ArrayKit;
import client.utils.DialogUtil;
import client.utils.IntBytesConverter;
import client.utils.crypto.SSL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPConnection {
    private InputStream in;
    private OutputStream out;
    private boolean isSecure;
    private SSL ssl;

    private final Thread thread;

    public TCPConnection(final TCPConnectInterface eventListener, final String ip, final int port) throws IOException {
        this(eventListener, new Socket(ip, port));
    }

    public TCPConnection(final TCPConnectInterface eventListener, final Socket socket) {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ssl = new SSL();
                isSecure = true;
                try {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                    sendKeyForSSL(ssl.protectKey());
                    while (!thread.isInterrupted() && !socket.isClosed()) {
                        byte[] flagB = in.readNBytes(4);
                        if (flagB.length==0) return;
                        int flagI = IntBytesConverter.convertToInt(flagB);
                        switch (flagI){
                            case Global.LOG_ENTER:
                                eventListener.logging(responseParser(in));
                                break;
                            case Global.LOG_STATUS:
                                eventListener.loggingStatus(responseParser(in));
                                break;
                            case Global.LOGGED:
                                eventListener.logAllow(responseParser(in));
                                break;
                            case Global.LOG_ERROR:
                                eventListener.logError(responseParser(in));
                                break;
                            case Global.LOAD_ATTACHMENT_RES:
                                eventListener.loadAttachmentRes(responseParser(in));
                                break;
                            case Global.LOAD_USERS_RES:
                                eventListener.loadedUsers(responseParser(in));
                                break;
                            case Global.SIGN_SUCCESS:
                                eventListener.signUpSuccess(responseParser(in));
                                break;
                            case Global.CONVERSATION_INIT:
                                eventListener.fetchConverserCertificates(responseParser(in));
                                break;
                            case Global.SEND_MESSAGE:
                                eventListener.parseNewMessage(responseParser(in));
                                break;
                            case Global.EDIT_PERSONAL_DATA_SUCCEEDED:
                                eventListener.personalDataUpdated(responseParser(in));
                                break;
                            case Global.FIND_PROFILE_TIP:
                                eventListener.findProfileTip(responseParser(in));
                                break;
                            case Global.FIND_PROFILE_BY_ID:
                                eventListener.fillProfile(responseParser(in));
                                break;
                            case Global.LOAD_DEVICES_LIST_RES:
                                eventListener.fillDevicesList(responseParser(in));
                                break;
                            case Global.FORCED_LOGOUT:
                                eventListener.forcedLogout();
                                break;
                            case Global.LIST_OF_POSSIBLE_CHOICES_RES:
                                eventListener.fetchListOfPossibleDialogs(responseParser(in));
                                break;
                            case Global.BURN_MESSAGES_DONE:
                                eventListener.messagesBurn(responseParser(in));
                                break;
                            default: break;
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        });
        this.thread.start();
    }

   private void sendKeyForSSL(byte[] key){
        this.sendMessage(ArrayKit.joinArraysWithConst(Global.MAKE_SSL, key));
    }

    private boolean requestBuilder(byte[] req, int command){
        if (!isSecure) return this.sendMessage(ArrayKit.joinArraysWithConst(command, req));
        byte[] lrEnc = ssl.encrypt(req);
        byte[] request = ArrayKit.joinArraysWithConst(command, lrEnc);
        return this.sendMessage(request);
    }

    private byte[] responseParser(InputStream in) {
        try {
            byte[] reqSize = in.readNBytes(4);
            byte[] a = in.readNBytes(IntBytesConverter.convertToInt(reqSize));

            if (a.length > 0) return (isSecure)? ssl.decrypt(a):a;

        } catch (IOException e) {
            DialogUtil.dialogError("Problem wczytywania danych od serwera.");
        }
        return null;
    }

    public void findProfile(String name) {
        this.requestBuilder(name.getBytes(), Global.FIND_PROFILE);
    }

    public void logenter(byte[] req){
        this.requestBuilder(req, Global.LOG_ENTER);
    }

    public void signingUp(byte[] req) {
        this.requestBuilder(req, Global.SIGNING_UP);
    }

    public void sendSigningConfirmation(byte[] req) {
        this.requestBuilder(req, Global.SIGN_CONFIRM);
    }

    public void listOfUsersEnter() {
        this.requestBuilder(new byte[0], Global.LOAD_USERS_REQ);
    }

    public boolean sendNewMessage(byte[] msg) {
        return this.requestBuilder(msg, Global.SEND_MESSAGE);
    }

    public synchronized boolean sendMessage(final byte[] msg) {
        try {
            this.out.write(msg);
            return true;
        } catch (IOException e) {
            DialogUtil.dialogError("Problem połączenia z serwerem");
            return false;
        }
    }

    public void disconnect() {
        this.requestBuilder(new byte[0], Global.DISCONNECTION);
        this.isSecure = false;
    }

    public void sendLoggingConfirmation(byte[] req) {
        this.requestBuilder(req, Global.LOGGING);
    }

    public void conversationInit(byte[] id) {
        this.requestBuilder(id, Global.CONVERSATION_INIT);
    }

    public void editProfile(byte[] req) {
        this.requestBuilder(req, Global.EDIT_PERSONAL_DATA);
    }

    public void findProfileById(String id) {
        this.requestBuilder(id.getBytes(), Global.FIND_PROFILE_BY_ID);
    }

    public void addContact(byte[] id) {
        this.requestBuilder(id, Global.ADD_CONTACT);
    }

    public void loadDevicesList() {
        this.requestBuilder(new byte[0], Global.LOAD_DEVICES_LIST_REQ);
    }

    public void changeCertificateStatus(String id) {
        this.requestBuilder(id.getBytes(), Global.CHANGE_CERTIFICATE_STATUS_REQ);
    }

    public void listOfPossibleChoices() {
        this.requestBuilder(new byte[0], Global.LIST_OF_POSSIBLE_CHOICES_REQ);
    }

    public void fireMessages(String id) {
        this.requestBuilder(id.getBytes(), Global.BURN_MESSAGES);
    }

    public void logout() {
        this.requestBuilder(new byte[0], Global.LOG_OUT);
    }

    public void loadAttachment(String id) {
        this.requestBuilder(id.getBytes(), Global.LOAD_ATTACHMENT_REQ);
    }

    public void resetMessagesReadState(String id) {
        this.requestBuilder(id.getBytes(), Global.RESET_MESSAGES_READ_STATE);
    }

    public void removeContact(byte[] id) {
        this.requestBuilder(id, Global.REMOVE_FROM_CONTACT_LIST);
    }
}