package controllers;

import client.App;
import client.Client;
import client.utils.ArrayKit;
import client.utils.ByteHexStringConverter;
import client.utils.crypto.AES;
import controllers.included.*;
import controllers.included.bridge.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainViewController {
    public MenuButton           menuButton;
    public MenuItem             loggedAs;
    private AboutView           aboutView;
    private ContactsView        contactsView;
    private DevicesView         devicesView;
    private MessengerView       messengerView;
    private ProfileView         profileView;
    private PreferencesView     preferencesView;
    private DevicesController   devicesController;
    private MessengerController messengerController;

    public DevicesController getDevicesController() {
        return devicesController;
    }

    public void setDevicesController(DevicesController devicesController) {
        this.devicesController = devicesController;
    }

    public void setMessengerController(MessengerController messengerController) {
        this.messengerController = messengerController;
    }

    public MessengerController getMessengerController() {
        return messengerController;
    }

    private ProfileController profileController;
    private Contacts contactsController;

    public void setProfileController(ProfileController profileController) {
        this.profileController = profileController;
    }

    public ProfileController getProfileController() {
        return profileController;
    }

    public void setContactsController(Contacts contactsController) {
        this.contactsController = contactsController;
    }

    public Contacts getContactsController() {
        return contactsController;
    }

    @FXML
    public BorderPane rootMain;

    @FXML
    public void initialize(){
        Client.getInstance().setMainViewController(this);
        this.aboutView = new AboutView();
        this.contactsView = new ContactsView();
        this.devicesView = new DevicesView();
        this.messengerView = new MessengerView();
        this.profileView = new ProfileView();
        this.preferencesView = new PreferencesView();
        this.loggedAs.setText("Zalogowany jako " + Client.getInstance().getUser().getFullName());

        this.updateCenter(this.profileView);
        this.updateLoggedProfilePhoto();
    }

    public void updateLoggedProfilePhoto(){
        Image img = new Image(new ByteArrayInputStream(ByteHexStringConverter.hexStringToBytes(
                Client.getInstance().getUser().getUserPhoto())));
        ImageView image = new ImageView(img);
        image.setFitHeight(62);
        image.setFitWidth(62);
        this.menuButton.setGraphic(image);
    }

    private void updateCenter(IncludedView includedView){
        FXMLLoader loader = includedView.getLoader();
        try {
            this.rootMain.setCenter(loader.load());
            ControllerInterface activeViewController = loader.getController();
            activeViewController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getStage(){
        return (Stage) this.rootMain.getScene().getWindow();
    }

    public void logout() {
        Client.getInstance().logout();
        Stage stage = (Stage) this.rootMain.getScene().getWindow();
        try {
            Client.resetClient();
            AnchorPane root = FXMLLoader.load(App.class.getResource("view/authorization.fxml"));
            Scene scene = new Scene(root, 600, 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setMessengerView() {
        this.updateCenter(this.messengerView);
    }

    @FXML
    public void setDevicesView() {
        this.updateCenter(this.devicesView);
    }

    @FXML
    public void setContactsView() {
        this.updateCenter(this.contactsView);
    }

    @FXML
    public void setPreferencesView() {
        this.updateCenter(this.preferencesView);
    }

    @FXML
    public void setAboutView() {
        this.updateCenter(this.aboutView);
    }

    @FXML
    public void setProfileView() {
        this.updateCenter(this.profileView);
    }

    @FXML
    public void showMyProfile() {
        this.updateCenter(this.profileView);
    }

    public void saveAttachment(byte[] res) {
        AES aes = new AES();
        ArrayKit.pointToStart();
        byte[] attachment = ArrayKit.pullNextPartFromArray(res);
        byte[] senderPubKey = ArrayKit.pullNextPartFromArray(res);
        ArrayKit.pointToStart();
        String att = new String(attachment);
        byte[] attBytes = ByteHexStringConverter.hexStringToBytes(att);
        byte[] encAtt = ArrayKit.pullNextPartFromArray(attBytes);
        byte[] encKey = ArrayKit.pullNextPartFromArray(attBytes);
        byte[] eSign  = ArrayKit.pullNextPartFromArray(attBytes);
        byte[] decAttachment;
        if (aes.eSignCheck(eSign,
                           new String(senderPubKey),
                           ByteHexStringConverter.hexStringToBytes(
                                   new String(encKey))
                           )){
            decAttachment = aes.decrypt(encAtt, ByteHexStringConverter.hexStringToBytes(new String(encKey)));
            Stage stage = (Stage) this.menuButton.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("pdf", "*.pdf"));
            byte[] finalDecAttachment = decAttachment;
            Platform.runLater(()->{
                File file = fileChooser.showSaveDialog(stage);
                try {
                    Files.write(Paths.get(file.getAbsolutePath()), finalDecAttachment);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
