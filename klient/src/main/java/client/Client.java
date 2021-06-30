package client;

import client.models.EncMsg;
import client.models.FetchedCertificate;
import client.models.User;
import client.models.UserShort;
import client.utils.*;
import client.utils.crypto.RSA;
import controllers.MainViewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Client implements TCPConnectInterface {
    private static Client client;

    public static Client getInstance(){
        if (Client.client != null) return Client.client;
        return new Client();
    }

    public static void resetClient(){
        Client.client = new Client();
    }

    private static final String IPServer   = "localhost";
    private static final int    portServer = 8888;
    private TCPConnection       connection;
    private MainViewController  mainViewController;
    private User                user;
    private ArrayList<User>     contacts;

    public ArrayList<User> getContacts() {
        return contacts;
    }

    public Client(){
        try {
            client = this;
            this.user = new User();
            this.contacts = new ArrayList<>();
            this.connection = new TCPConnection(this, IPServer, portServer);
        } catch (IOException e) {
            DialogUtil.dialogError("Nie mogę nawiązać połączenia z serwerem. Sprawdź połączenie z internetem. Sprawdź port 8888");
        }
    }

    public void conversationInit(String id){
        byte[] n = id.getBytes();
        this.connection.conversationInit(n);
    }

    @Override
    public void loadedUsers(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        int length = IntBytesConverter.convertToInt(ArrayKit.pullNextPartFromArray(x));
        while (length!=0){
            boolean isOnline = new String(ArrayKit.pullNextPartFromArray(x)).equals("1");
            String id = new String(ArrayKit.pullNextPartFromArray(x));
            String identificator = new String(ArrayKit.pullNextPartFromArray(x));
            String fullName = new String(ArrayKit.pullNextPartFromArray(x));
            String userPhoto = new String(ArrayKit.pullNextPartFromArray(x));
            String email = new String(ArrayKit.pullNextPartFromArray(x));
            String description = new String(ArrayKit.pullNextPartFromArray(x));
            String phoneNumber = new String(ArrayKit.pullNextPartFromArray(x));
            String gotNewMsg = new String(ArrayKit.pullNextPartFromArray(x));
            String lastSeen = new String(ArrayKit.pullNextPartFromArray(x));
            String isContact = new String(ArrayKit.pullNextPartFromArray(x));
            this.contacts.add(
                    new User(isOnline, id, identificator, fullName, userPhoto, email, description, phoneNumber, gotNewMsg, lastSeen, isContact.equals("1")));
            length--;
        }
        Platform.runLater(()->{
            if (this.mainViewController.getContactsController() != null)
                this.mainViewController.getContactsController().setContactList(this.contacts);
        });
    }

    @Override
    public void fetchConverserCertificates(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        int length = IntBytesConverter.convertToInt(ArrayKit.pullNextPartFromArray(x));
        ArrayList<FetchedCertificate> arr = new ArrayList<>();
        while (length != 0){
            String id = new String(ArrayKit.pullNextPartFromArray(x));
            String key = new String(ArrayKit.pullNextPartFromArray(x));
            FetchedCertificate certificate = new FetchedCertificate(id, key);
            arr.add(certificate);
            length--;
        }
        if (this.mainViewController.getMessengerController() != null){
            this.mainViewController.getMessengerController().setActualCertificatesList(arr);
        }
    }

    @Override
    public void parseNewMessage(byte[] msg) {
        new EncMsg(msg);
    }

    @Override
    public void logAllow(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        this.user = new User(x);
        this.user.setIam(true);
        this.logged();
    }

    @Override
    public void logError(byte[] res) {
        Platform.runLater(()->{
//            SystemInfo systemInfo = new SystemInfo();
            DialogUtil.dialogError("Odmowa dostępu do serwisu. Użytkownik z tym loginem zabłokował" +
                            " urządzenie \n");
//                    systemInfo.getHardware().getComputerSystem().getManufacturer() + " " +
//                    systemInfo.getHardware().getComputerSystem().getModel());
        });
    }

    @Override
    public void personalDataUpdated(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        this.user = new User(x);
        this.user.setIam(true);
        Platform.runLater(()-> this.mainViewController.setProfileView());
    }

    @Override
    public void findProfileTip(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        int size = IntBytesConverter.convertToInt(ArrayKit.pullNextPartFromArray(x));
        ArrayList<UserShort> arr = new ArrayList<>();
        while(size != 0){
            byte[] id = ArrayKit.pullNextPartFromArray(x);
            byte[] identificator = ArrayKit.pullNextPartFromArray(x);
            byte[] email = ArrayKit.pullNextPartFromArray(x);
            byte[] full_name = ArrayKit.pullNextPartFromArray(x);
            arr.add(new UserShort(new String(id), new String(identificator), new String(full_name), new String(email)));
            size--;
        }

        if (this.mainViewController.getProfileController() != null)
            this.mainViewController.getProfileController().fillAutoCompleteBox(arr);
        if (this.mainViewController.getContactsController()!= null)
            this.mainViewController.getContactsController().fillAutoCompleteBox(arr);
    }

    @Override
    public void fillProfile(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        User user = new User(x);
        user.setIam(false);
        Platform.runLater(()->{
            this.mainViewController.setProfileView();
            this.mainViewController.getProfileController().showUser(user);
        });
    }

    @Override
    public void fillDevicesList(byte[] res){
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        int size = IntBytesConverter.convertToInt(ArrayKit.pullNextPartFromArray(x));
        ArrayList<FetchedCertificate> arr = new ArrayList<>();
        while(size != 0){
            String id = new String(ArrayKit.pullNextPartFromArray(x));
            String device_name = new String(ArrayKit.pullNextPartFromArray(x));
            String key = new String(ArrayKit.pullNextPartFromArray(x));
            String blocked = new String(ArrayKit.pullNextPartFromArray(x));
            String last_use = new String(ArrayKit.pullNextPartFromArray(x));
            arr.add(new FetchedCertificate(id, device_name, key, blocked, last_use));
            size--;
        }
        Platform.runLater(()->{
            if (this.mainViewController.getDevicesController() != null)
                this.mainViewController.getDevicesController().renderDevicesInfo(arr);
        });

    }

    @Override
    public void fetchListOfPossibleDialogs(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        int length = IntBytesConverter.convertToInt(ArrayKit.pullNextPartFromArray(x));
        ArrayList<User> list = new ArrayList<>();
        while (length!=0){
            boolean isOnline = new String(ArrayKit.pullNextPartFromArray(x)).equals("1");
            String id = new String(ArrayKit.pullNextPartFromArray(x));
            String identificator = new String(ArrayKit.pullNextPartFromArray(x));
            String fullName = new String(ArrayKit.pullNextPartFromArray(x));
            String userPhoto = new String(ArrayKit.pullNextPartFromArray(x));
            String email = new String(ArrayKit.pullNextPartFromArray(x));
            String description = new String(ArrayKit.pullNextPartFromArray(x));
            String phoneNumber = new String(ArrayKit.pullNextPartFromArray(x));
            String gotNewMsg = new String(ArrayKit.pullNextPartFromArray(x));
            String lastSeen = new String(ArrayKit.pullNextPartFromArray(x));
            String isContact = new String(ArrayKit.pullNextPartFromArray(x));
            list.add(
                    new User(isOnline, id, identificator, fullName, userPhoto, email, description, phoneNumber, gotNewMsg, lastSeen, isContact.equals("1")));
            length--;
        }
        Platform.runLater(()-> this.mainViewController.getMessengerController().setDialogsList(list));

    }

    @Override
    public void messagesBurn(byte[] res) {
        Platform.runLater(()-> this.mainViewController.getMessengerController().burned(new String(res)));
    }

    @Override
    public void loadAttachmentRes(byte[] res) {
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        this.mainViewController.saveAttachment(x);
    }

    @Override
    public void forcedLogout() {
        this.logout();
        Platform.runLater(()->{
            DialogUtil.warningAlert("Urządzenie zostało zablokowane. Możesz " +
                    "odblokować dostęp do konta z innego swojego urządzenia ");
            Stage stage = this.mainViewController.getStage();
            try {
                AnchorPane root = FXMLLoader.load(App.class.getResource("view/authorization.fxml"));
                Scene scene = new Scene(root, 600, 600);
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void logging(byte[] res){
        byte[] x = new byte[res.length-4];
        System.arraycopy(res, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        byte[] enc1 = ArrayKit.pullNextPartFromArray(x);
        byte[] enc2 = ArrayKit.pullNextPartFromArray(x);
        byte[] decryptedWithServerKey = RSA.decryptWithServerKey(enc2);
        byte[] decrypted = RSA.decrypt(enc1);
        if (Arrays.equals(decryptedWithServerKey, decrypted))
            this.connection.sendLoggingConfirmation(decrypted);
    }

    @Override
    public void onDisconnect() {
        this.connection.disconnect();
        Platform.runLater(()->{
            Window current = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
            Stage stage = new Stage();
            try {
                Scene scene = new Scene(FXMLLoader.load(App.class.getResource("view/authorization.fxml")));
                stage.setScene(scene);
                assert current != null;
                current.hide();
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            } catch (IOException e) {
                System.exit(1);
            }
        });
    }

    public void disconnectImmediately(){
        this.connection.disconnect();
    }

    @Override
    public void loggingStatus(byte[] res) {
        if (res[4] == Global.LOGGED) logged();
        else return;
        ArrayKit.pointToStart();
        byte[] id = ArrayKit.pullNextPartFromArray(res);
        byte[] email = ArrayKit.pullNextPartFromArray(res);
        byte[] fullName = ArrayKit.pullNextPartFromArray(res);
        byte[] phoneNumber = ArrayKit.pullNextPartFromArray(res);
        byte[] description = ArrayKit.pullNextPartFromArray(res);
        byte[] userphoto = ArrayKit.pullNextPartFromArray(res);
        byte[] isOnline = ArrayKit.pullNextPartFromArray(res);
        this.user.setPhoneNumber(new String(phoneNumber));
        this.user.setFullName(new String(fullName));
        this.user.setEmail(new String(email));
        this.user.setDescription(new String(description));
        this.user.setUserPhoto(new String(userphoto));
        this.user.setIdentificator(new String(id));
        this.user.setIam(true);
        this.user.setOnline(new String(isOnline).equals("1"));
    }

    public void logenter(byte[] req) {
        this.connection.logenter(req);
    }

    private void logged(){
        Platform.runLater(()->{
            Window current = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
            Stage stage = new Stage();
            try {
                assert current != null;
                current.hide();
                BorderPane root = FXMLLoader.load(App.class.getResource("view/mainview.fxml"));
                Scene scene = new Scene(root, 1024, 700);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    public void signingUp(byte[] sign) {
        this.connection.signingUp(sign);
    }

    @Override
    public void signUpSuccess(byte[] res) {
        try (FileOutputStream fos = new FileOutputStream(Objects.requireNonNull(PrivateFolderGen.name()));
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            oos.writeBytes(new String(res));
        } catch (Exception e) {
            DialogUtil.dialogError("Błąd rejestracji. Nie mogę stworzyć pliku " + PrivateFolderGen.name());
            return;
        }
        Platform.runLater(this::signedAllowedToLogIn);
    }

    public void findProfile(String name) {
        this.connection.findProfile(name);
    }

    public void signedAllowedToLogIn(){
        Window current = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        Stage stage = (Stage) current;
        try {
            AnchorPane root = FXMLLoader.load(App.class.getResource("view/authorization.fxml"));
            Scene scene = new Scene(root, 600, 600);
            assert stage != null;
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void listOfUsersEnter() {
        this.connection.listOfUsersEnter();
    }

    public boolean sendMessage(byte[] msg) {
        return this.connection.sendNewMessage(msg);
    }

    public void sendSigningConfirmation(byte[] bytes) {
        this.connection.sendSigningConfirmation(bytes);
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

   public User getUser(){
        return this.user;
    }

    public void editProfile(String spec) {
        byte[] req = spec.getBytes();
        this.connection.editProfile(req);
    }

    public void findProfileById(String id) {
        this.connection.findProfileById(id);
    }

    public void addContact(byte[] id) {
        this.connection.addContact(id);
    }

    public MainViewController getMainViewController() {
        return mainViewController;
    }

    public void loadDevicesList() {
        this.connection.loadDevicesList();
    }

    public void changeCertificateStatus(String id) {
        this.connection.changeCertificateStatus(id);
    }

    public void listOfPossibleChoices() {
        this.connection.listOfPossibleChoices();
    }

    public void fireMessages(String id) {
        this.connection.fireMessages(id);
    }

    public void logout() {
        this.connection.logout();
    }

    public void loadAttachment(String id) {
        this.connection.loadAttachment(id);
    }

    public void resetMessagesReadState(String id) {
        this.connection.resetMessagesReadState(id);
    }

    public void removeContact(byte[] id) {
        this.connection.removeContact(id);
    }
}
