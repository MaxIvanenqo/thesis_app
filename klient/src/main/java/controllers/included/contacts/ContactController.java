package controllers.included.contacts;

import client.App;
import client.Client;
import client.models.User;
import client.utils.ByteHexStringConverter;
import controllers.included.Contacts;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public class ContactController implements ContactControllerInterface {

    public Button userPhoto;
    public Label fullName;
    public Label email;
    public Label phone;
    public Button profileLink;
    public Label lastSeen;
    private String id;

    public ImageView isOffline;
    public ImageView isOnline;

    public void initialize(){

    }

    @Override
    public void fillData(User user){
        this.id = user.getIdentificator();
        this.fullName.setText(user.getFullName());
        this.email.setText(user.getEmail());
        this.phone.setText(user.getPhoneNumber());
        this.renderPhoto(user);
        this.isOnline.setVisible(user.isOnline());
        this.isOffline.setVisible(!user.isOnline());
        this.lastSeen.setVisible(!user.isOnline());
        if (!user.isOnline()) this.lastSeen.setText("ostatni raz online: " + user.getLastSeen().substring(0, 16));
    }

    private void renderPhoto(User user){
        this.userPhoto.setId(user.getIdentificator());
        Image img = new Image(new ByteArrayInputStream(ByteHexStringConverter.hexStringToBytes(user.getUserPhoto())));
        ImageView image = new ImageView(img);
        image.setFitHeight(100.0);
        image.setFitWidth(100.0);
        image.setPreserveRatio(true);
        this.userPhoto.setGraphic(image);
        this.userPhoto.setOnAction(event-> Client.getInstance().findProfileById(this.userPhoto.getId()));
    }

    public void showProfile() {
        Client.getInstance().findProfileById(this.id);
    }
}
