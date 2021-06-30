package controllers.included;

import client.App;
import client.Client;
import client.models.DecryptedMessage;
import client.models.EncMsg;
import client.models.FetchedCertificate;
import client.models.User;
import client.utils.ByteHexStringConverter;
import client.utils.DialogUtil;
import controllers.MainViewController;
import controllers.included.message.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MessengerController implements ControllerInterface {
    public HBox contactList;
    public StackPane dialogPaneStackPane;
    public ScrollPane scrollPaneMessages;
    public AnchorPane scrollPaneMessagesHide;
    public HBox DialogControls;
    public VBox dialogControlsAsidePanel;
    public TextArea inputField;
    public VBox attachmentsField;
    public Button deleteAttachmentBtn;
    private ArrayList<FetchedCertificate> actualCertificates;
    public VBox messagesField;
    private HashSet<String> buttonIdHashSet;

    private User target;

    private Attachment attachment;

    @FXML
    public void initialize(){
        Client.getInstance().listOfPossibleChoices();
        this.scrollPaneMessages.vvalueProperty().bind(this.messagesField.heightProperty());
    }

    public void setDialogsList(ArrayList<User> arr){
        arr.forEach((el)->{
            Button button = this.renderButton(el);
            this.buttonIdHashSet.add(button.getId());
            if (el.isGotNewMsg()) button.getStyleClass().add("new-messages-available");
            this.contactList.getChildren().add(button);
        });
    }

    public void startDialogInitializedFromProfileView(User target){
        this.target = target;
        Button b = renderButton(target);
        System.out.println(Arrays.toString(buttonIdHashSet.toArray()));
        System.out.println(b.getId());
        if (this.buttonIdHashSet.add(b.getId()))
            this.contactList.getChildren().add(b);
        scrollPaneMessages.toFront();
        this.DialogControls.setVisible(true);
        this.dialogControlsAsidePanel.setVisible(true);
        Client.getInstance().conversationInit(this.target.getId());
    }

    private Button renderButton(User user){
        if (this.buttonIdHashSet == null) this.buttonIdHashSet = new HashSet<>();
        Button button = new Button();
        button.setId(user.getIdentificator());
        this.buttonIdHashSet.add(button.getId());
        Image img = new Image(new ByteArrayInputStream(ByteHexStringConverter.hexStringToBytes(user.getUserPhoto())));
        ImageView image = new ImageView(img);
        image.setFitHeight(40.0);
        image.setFitWidth(40.0);
        image.setPreserveRatio(true);
        button.setGraphic(image);
        button.setId(user.getIdentificator());
        button.getStyleClass().add("contact-btn");
        button.setOnAction((ActionEvent evt)->{
            scrollPaneMessages.toFront();
            this.DialogControls.setVisible(true);
            this.dialogControlsAsidePanel.setVisible(true);
            this.target = user;
            Client.getInstance().conversationInit(this.target.getId());
        });
        return button;
    }

    @Override
    public void setMainController(MainViewController mainViewController) {
        mainViewController.setMessengerController(this);
    }

    public void closeDialogPane() {
        this.scrollPaneMessagesHide.toFront();
        this.DialogControls.setVisible(false);
        this.dialogControlsAsidePanel.setVisible(false);
        Client.getInstance().resetMessagesReadState(this.target.getId());
    }

    public void setActualCertificatesList(ArrayList<FetchedCertificate> arr) {
        this.actualCertificates = arr;
    }

    public void messageCreator(DecryptedMessage dm){
        System.out.println("targetId: " + target.getId());
        if (
            dm.getDialogTargets().equals(target.getId()+"_"+Client.getInstance().getUser().getId()) ||
            dm.getDialogTargets().equals(Client.getInstance().getUser().getId()+"_"+target.getId())
        ){
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("view/messenger/message.fxml"));
                HBox hBox = loader.load();
                MessageController messageController = loader.getController();
                messageController.setMessengerController(this);
                messageController.setMessage(dm);
                this.messagesField.getChildren().add(hBox);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessageBox(VBox msg){
        this.messagesField.getChildren().add(msg);
    }

    public void sendMessage() {
        String message = this.inputField.getText().trim();
        if (message.equals("")) return;
        byte[] msg = message.getBytes();
        this.actualCertificates.forEach(certificate -> {
            EncMsg em = new EncMsg(msg , certificate, this.target.getId() + "_" + Client.getInstance().getUser().getId(), this.attachment);
            byte[] M = em.toBytes();
            if (Client.getInstance().sendMessage(M)){
                this.inputField.clear();
                this.attachmentsField.setVisible(false);
                this.deleteAttachmentBtn.setVisible(false);
            }
            else{
                DialogUtil.dialogError("Błąd podczas wysłania wiadomości.");
            }
        });
    }

    public User getTarget() {
        return target;
    }

    public void fireMessages() {
        if (DialogUtil.confirmationAlert("Usunąć historię z " +
                "konwersacji z "+target.getFullName()+"? \n(Operacja jest nieodwracalna)")){
            Client.getInstance().fireMessages(target.getId());
        }
    }

    public void burned(String target) {
        if (!target.trim().equals(this.target.getId().trim())) return;
        this.messagesField.getChildren().clear();
    }

    public void attachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("pliki pdf", "*.pdf"),
                new FileChooser.ExtensionFilter("pliki graficzne", "*.jpg", ".jpeg" , "*.png"),
                new FileChooser.ExtensionFilter("pliki tekstowe", "*.txt", "*.doc", "*.docx")
        );
        Stage stage = (Stage) this.contactList.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        this.attachment = new Attachment(file);
        Button button = new Button();
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.getStyleClass().add("choosen-file-attachment");
        button.setTooltip(new Tooltip(file.getName()));
        button.setPrefHeight(40.0);
        button.setPrefWidth(40.0);
        this.attachmentsField.getChildren().clear();
        this.attachmentsField.getChildren().add(button);
        this.attachmentsField.setVisible(true);
        this.deleteAttachmentBtn.setVisible(true);
    }

    public void deleteAttachment() {
        if (this.attachment != null) {
            this.attachment = null;
            this.attachmentsField.getChildren().clear();
            this.attachmentsField.setVisible(false);
            this.deleteAttachmentBtn.setVisible(false);
        }
    }
}
