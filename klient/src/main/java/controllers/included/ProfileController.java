package controllers.included;

import client.App;
import client.Client;
import client.models.User;
import client.models.UserShort;
import client.utils.ArrayKit;
import client.utils.ByteHexStringConverter;
import client.utils.IntBytesConverter;
import controllers.MainViewController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ProfileController implements ControllerInterface {
    public TextArea descriptionField;
    public Button userPhotoBtnBg;
    public Label fullNameLabel;
    public Label myidLabel;
    public HBox hboxPhone;
    public TextField phoneNumberLabel;
    public TextField emailLabel;
    public Button descriptionEditBtn;
    public Button phoneEditBtn;
    public Pane editBox;
    public VBox textFieldEditBox;
    public TextField searchBox;
    public VBox autoCompletedBox;
    public Button fullNameEditBtn;
    public Button addContactBtn;
    public Button startChatBtn;
    public Button remContactBtn;

    private TextInputControl textInputControl;
    private User user;
    private User userVisible;
    private MainViewController mainViewController;

    @FXML
    public void initialize(){
        this.user = Client.getInstance().getUser();
        this.showUser(this.user);
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")){
                Client.getInstance().findProfile(newValue);
            }
            this.autoCompletedBox.getChildren().clear();
        });
    }

    private void switchVisibility(Button[] btnGroup, User user){
        for (Button btn: btnGroup) {
            btn.setVisible(user.isIam());
        }
    }

    public void showUser(User user){
        System.out.println("show " + user.getEmail() + user.isContact());
        this.userVisible = user;
        this.switchVisibility(new Button[]{fullNameEditBtn, descriptionEditBtn, phoneEditBtn, descriptionEditBtn}, user);
        this.addContactBtn.setVisible(!user.isIam() && !user.isContact());
        this.remContactBtn.setVisible(!user.isIam() && user.isContact());
        this.startChatBtn.setVisible(!user.isIam());
        this.fullNameLabel.setText(user.getFullName());
        this.emailLabel.setText(user.getEmail());
        this.myidLabel.setText("@"+user.getIdentificator());
        this.phoneNumberLabel.setText(user.getPhoneNumber());
        this.descriptionField.setText(user.getDescription());
        Image img = new Image(new ByteArrayInputStream(ByteHexStringConverter.hexStringToBytes(user.getUserPhoto())));
        ImageView image = new ImageView(img);
        image.setFitHeight(160.0);
        image.setFitWidth(160.0);
        image.getStyleClass().add("main-picture");
        this.userPhotoBtnBg.setGraphic(image);
    }

    @Override
    public void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        this.mainViewController.setProfileController(this);
    }

    public void editField(ActionEvent actionEvent) {
        String type = ((Button)actionEvent.getSource()).getText();
        this.editBox.setVisible(true);
        this.textFieldEditBox.setId(type);
        switch (type){
            case "full_name":
            case "identificator":
            case "email_address":
            case "phone_number":
                this.textInputControl = new TextField();
                this.textInputControl.getStyleClass().add("edit-text-field");
                this.textFieldEditBox.getChildren().add(this.textInputControl);
                break;
            case "description":
                this.textInputControl = new TextArea();
                this.textInputControl.getStyleClass().add("edit-text-field");
                this.textFieldEditBox.getChildren().add(this.textInputControl);
                break;
        }

    }

    public void fillAutoCompleteBox(ArrayList<UserShort> arr){
        Platform.runLater(()-> arr.forEach((userShort)->{
            if (userShort.getIdentificator().equals(this.user.getIdentificator())) return;
            HBox hBox = new HBox();
            Button button = new Button();
            button.getStyleClass().add("autocomplete-find-btn");
            button.setId(userShort.getIdentificator());
            button.setText("Imię: "+ userShort.getFullName()+", id: "+userShort.getId());
            button.setTooltip(new Tooltip(userShort.getEmail()));
            hBox.getChildren().add(button);
            button.setOnAction((event)-> Client.getInstance().findProfileById(button.getId()));
            this.autoCompletedBox.getChildren().add(hBox);
        }));
    }

    public void changeUserPhoto() {
        if (!this.userVisible.isIam()) return;
        this.editBox.setVisible(true);
        Stage stage = (Stage) this.userPhotoBtnBg.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("png images", "*.png"),
                new FileChooser.ExtensionFilter("jpg images", "*.jpg")
        );
        File selected = fileChooser.showOpenDialog(stage);
        try {
            byte[] f = Files.readAllBytes(Path.of(selected.getAbsolutePath()));
            String fs = ByteHexStringConverter.bytesToStringHex(f);
            Label imagePathEditBox = new Label(selected.getName());
            this.textFieldEditBox.getChildren().add(imagePathEditBox);
            this.textInputControl = new TextField(fs);
            this.textFieldEditBox.setId("user_photo");
            Client.getInstance().getUser().setUserPhoto(fs);
            this.mainViewController.updateLoggedProfilePhoto();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeEditBox() {
        this.textFieldEditBox.getChildren().clear();
        this.editBox.setVisible(false);
    }

    public void editConfirm() {
        Client.getInstance().editProfile(
                this.textFieldEditBox.getId() + ":line/value:" +
                        this.textInputControl.getText()
        );
    }

    public void startChat() {
        if (!userVisible.isIam()){
            this.mainViewController.setMessengerView();
            this.mainViewController.getMessengerController().startDialogInitializedFromProfileView(userVisible);
        }
    }

    public void remContact() {
        Client.getInstance().removeContact(
                ArrayKit.joinArrays(
                        IntBytesConverter.convertToBytes(userVisible.getId().getBytes().length),
                        userVisible.getId().getBytes(),
                        IntBytesConverter.convertToBytes(userVisible.getIdentificator().getBytes().length),
                        userVisible.getIdentificator().getBytes()
                )
        );
    }

    public void addContact() {
        Client.getInstance().addContact(
                ArrayKit.joinArrays(
                        IntBytesConverter.convertToBytes(userVisible.getId().getBytes().length),
                        userVisible.getId().getBytes(),
                        IntBytesConverter.convertToBytes(userVisible.getIdentificator().getBytes().length),
                        userVisible.getIdentificator().getBytes()
                )
        );
    }

}
