package wwsis.ivanenqo.server.utils.configs;

import wwsis.ivanenqo.server.models.User;
import wwsis.ivanenqo.server.utils.*;
import wwsis.ivanenqo.server.utils.crypto.RSA;
import wwsis.ivanenqo.server.utils.crypto.SSL;

import java.io.*;
import java.net.Socket;

import static wwsis.ivanenqo.Commands.*;

public class TCPConnection {
    private InputStream in;
    private OutputStream out;
    private SSL ssl;
    private boolean isSecure;
    private final TCPConnectInterface server;

    private final Thread thread;
    private final Socket socket;
    private User user;


    public TCPConnection(final TCPConnectInterface eventListener, final Socket socket) {
        this.connectionEstablished(eventListener, this);
        this.server = eventListener;
        this.socket = socket;
        setSecure(false);
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                    while (!thread.isInterrupted() && !socket.isClosed()) {
                        byte[] flagB = in.readNBytes(4);
                        if (flagB.length==0) break;
                        int flagI = IntByteArray.convertToInt(flagB);
                        switch (flagI) {
                            case MAKE_SSL:
                                ssl = new SSL(RSA.decrypt(requestParser(in)));
                                setSecure(true);
                                break;
                            case LOG_OUT:
                                logout(eventListener);
                                break;
                            case DISCONNECTION:
                                killConnection(eventListener);
                                break;
                            case LOG_ENTER:
                                logEnter(eventListener, requestParser(in));
                                break;
                            case LOGGING:
                                logging(requestParser(in), eventListener);
                                break;
                            case SIGNING_UP:
                                signingUp(eventListener, requestParser(in));
                                break;
                            case LOAD_USERS_REQ:
                                groupUsers(eventListener);
                                break;
                            case SIGN_CONFIRM:
                                checkToken(eventListener, requestParser(in));
                                break;
                            case SEND_MESSAGE:
                                messageReceived(eventListener, requestParser(in));
                                break;
                            case RESET_MESSAGES_READ_STATE:
                                resetMessagesReadState(eventListener, requestParser(in));
                                break;
                            case LOAD_ATTACHMENT_REQ:
                                loadAttachment(eventListener, requestParser(in));
                                break;
                            case CONVERSATION_INIT:
                                conversationInit(eventListener, requestParser(in));
                                break;
                            case EDIT_PERSONAL_DATA:
                                editPersonalData(eventListener, requestParser(in));
                                break;
                            case FIND_PROFILE:
                                findProfile(eventListener, requestParser(in));
                                break;
                            case FIND_PROFILE_BY_ID:
                                findProfileById(eventListener, requestParser(in));
                                break;
                            case ADD_CONTACT:
                                addContact(eventListener, requestParser(in));
                                break;
                            case REMOVE_FROM_CONTACT_LIST:
                                removeContact(eventListener, requestParser(in));
                                break;
                            case LOAD_DIALOG:
                                loadDialog(eventListener, requestParser(in));
                                break;
                            case LOAD_DEVICES_LIST_REQ:
                                loadDevicesList(eventListener);
                                break;
                            case CHANGE_CERTIFICATE_STATUS_REQ:
                                changeCertificateStatus(eventListener, requestParser(in));
                                break;
                            case LIST_OF_POSSIBLE_CHOICES_REQ:
                                listOfPossibleContactChoices(eventListener);
                                break;
                            case BURN_MESSAGES:
                                burnMessages(eventListener, requestParser(in));
                                break;
                            default:
                                break;
                        }
                    }
                    killConnection(eventListener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.thread.start();
    }

    private void removeContact(TCPConnectInterface eventListener, byte[] req) {
        eventListener.removeContact(req, this);
    }

    private void resetMessagesReadState(TCPConnectInterface eventListener, byte[] req) {
        eventListener.resetMessagesReadState(req, this);
    }

    private void loadAttachment(TCPConnectInterface eventListener, byte[] req) {
        eventListener.loadAttachment(req, this);
    }

    private void logout(TCPConnectInterface eventListener) {
        if (this.user == null) {
            try {
                this.socket.close();
                this.thread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        eventListener.logout(this);
    }

    private void burnMessages(TCPConnectInterface eventListener, byte[] req) {
        eventListener.burnMessages(this, req);
    }

    private void listOfPossibleContactChoices(TCPConnectInterface eventListener) {
        eventListener.listOfPossibleContactChoices(this);
    }

    private void changeCertificateStatus(TCPConnectInterface eventListener, byte[] req) {
        eventListener.changeCertificateStatus(req, this);
    }

    private void loadDevicesList(TCPConnectInterface eventListener) {
        eventListener.loadDeviceList(this);
    }

    private void loadDialog(TCPConnectInterface eventListener, byte[] req) {
        eventListener.loadDialog(this, req);
    }

    private void addContact(TCPConnectInterface eventListener, byte[] req) {
        eventListener.addContact(req, this);
    }

    private void findProfileById(TCPConnectInterface eventListener, byte[] req) {
        eventListener.findProfileById(req, this);
    }

    private void findProfile(TCPConnectInterface eventListener, byte[] req) {
        eventListener.findProfile(req, this);
    }

    private void editPersonalData(TCPConnectInterface eventListener, byte[] req) {
        eventListener.editPersonalData(this, req);
    }

    private void connectionEstablished(TCPConnectInterface eventListener, TCPConnection connection) {
        eventListener.connectionEstablished(connection);
    }

    public synchronized void makeResponse(final byte[] msg){
        try {
            if (this.isSecure)
                this.out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
            this.killConnection(server);
        }
    }

    public void buildResponse(byte[] res, int command){
        byte[] length = IntByteArray.convertToBytes(res.length);
        byte[] lr = ArrayKit.joinArrays(length, res);
        byte[] lrEnc = ssl.encrypt(lr);
        byte[] response = ArrayKit.joinArraysWithConst(command, lrEnc);
        this.makeResponse(response);
    }

    public Socket getSocket() {
        return socket;
    }

    public Thread getThread() {
        return thread;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSecure(boolean secure) {
        isSecure = secure;
    }

    private void groupUsers(TCPConnectInterface eventListener) {
        eventListener.groupUsers(this);
    }

    private byte[] requestParser(InputStream in){
        return this.readRequest(in);
    }

    private byte[] readRequest(InputStream in) {
       try {
            byte[] reqSize = in.readNBytes(4);
            byte[] a = in.readNBytes(IntByteArray.convertToInt(reqSize));
            if (a.length > 0) return (isSecure)? ssl.decrypt(a):a;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void conversationInit(TCPConnectInterface eventListener, byte[] id){
        eventListener.conversationInit(this, id);
    }

    private void messageReceived(TCPConnectInterface eventListener, byte[] bytes) {
        eventListener.onMessageGot(this, bytes);
    }

    private void logEnter(TCPConnectInterface eventListener, byte[] req){
        eventListener.logEnter(this, req);
    }

    private void signingUp(TCPConnectInterface eventListener, byte[] req) {
        eventListener.signingUp(this, req);
    }

    private void checkToken(TCPConnectInterface eventListener, byte[] req){
        eventListener.checkToken(this, req);
    }

    private void logging(byte[] req, TCPConnectInterface eventHandler){
        eventHandler.logging(req, this);
    }

    private void killConnection(final TCPConnectInterface eventListener){
        eventListener.logout(this);
    }
}