package controllers.included.message;

import client.App;
import client.models.DecryptedMessage;
import controllers.included.MessengerController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MessageController {
    public HBox msgBoxContainer;
    private MessengerController messengerController;
    private MessageDetailedController messageDetailedController;
    public void setMessengerController(MessengerController messengerController){
        this.messengerController = messengerController;
    }

    public MessengerController getMessengerController() {
        return messengerController;
    }

    public void setMessage(DecryptedMessage dm) {
        this.createMessage((dm.getOwner()?"sent":"received"), dm);
    }

    private void createMessage(String type, DecryptedMessage dm){
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/messenger/"+type+".fxml"));
        try {
            VBox msgBox = loader.load();
            this.messageDetailedController = loader.getController();
            this.messageDetailedController.setMessageController(this);
            this.messageDetailedController.fillMessageData(dm);
            this.msgBoxContainer.getChildren().add(msgBox);
            this.msgBoxContainer.setAlignment((type.equals("sent")? Pos.TOP_LEFT:Pos.TOP_RIGHT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
