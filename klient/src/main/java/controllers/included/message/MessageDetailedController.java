package controllers.included.message;

import client.App;
import client.Client;
import client.models.DecryptedMessage;
import client.utils.CopyToClipboard;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MessageDetailedController {
    public Label msg;
    public Label name;
    public Label time;
    public Label date;
    public VBox attachments;
    private MessageController messageController;
    public VBox msgBox;

    public void setMessageController(MessageController messageController){
        this.messageController = messageController;
    }

    public void fillMessageData(DecryptedMessage dm){
        String datetime = dm.getTime();
        String[] dateTimeArr = datetime.split(" ");
        String date = dateTimeArr[0];
        String time = dateTimeArr[1].substring(0, 5);
        this.msg.setText(dm.getMsg());
        this.name.setText(dm.getOwner()?"Ty":this.messageController.getMessengerController().getTarget().getFullName());
        this.time.setText(time);
        this.date.setText(date);
        this.messageController.getMessengerController().addMessageBox(this.msgBox);
        if (dm.getAttName()!=null){
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/messenger/attachment.fxml"));
            try {
                Button attBtn = loader.load();
                attBtn.setOnAction(evt-> {
                    Client.getInstance().loadAttachment(dm.getId());
                });
                attBtn.getStyleClass().addAll("attachment-pre-send");
                attBtn.setTooltip(new Tooltip("Pobierz plik " + dm.getAttName()));
                attBtn.setText(dm.getAttName());
                this.attachments.getChildren().add(attBtn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void copyToClipboard(ActionEvent actionEvent) {
        CopyToClipboard.makeCopy(this.msg.getText());
    }
}
