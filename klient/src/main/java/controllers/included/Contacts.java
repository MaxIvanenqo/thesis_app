package controllers.included;

import client.App;
import client.Client;
import client.models.User;
import client.models.UserShort;
import controllers.MainViewController;
import controllers.included.contacts.ContactControllerInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class Contacts implements ControllerInterface {
    public VBox contactsField;
    public StackPane stackPane;
    public ScrollPane scrollPaneContacts;
    public AnchorPane contactsListHide;
    public VBox findAutocompleteBox;
    public TextField searchProfileTextField;
    private ContactControllerInterface contactInterface;

    @FXML
    public void initialize(){
        Client.getInstance().listOfUsersEnter();
        searchProfileTextField.textProperty().addListener(((observableValue, s, t1) -> {
            if (!t1.equals("")){
                Client.getInstance().findProfile(t1);
            }
            this.findAutocompleteBox.getChildren().clear();
        }));
    }

    public void fillAutoCompleteBox(ArrayList<UserShort> arr){
        Platform.runLater(()-> arr.forEach((userShort)->{
            if (userShort.getIdentificator().equals(
                    Client.getInstance().getUser().getIdentificator())) return;
            HBox hBox = new HBox();
            Button button = new Button();
            button.getStyleClass().add("autocomplete-find-btn");
            button.setId(userShort.getIdentificator());
            button.setText("Imię: "+ userShort.getFullName()+", id: "+userShort.getId());
            hBox.getChildren().add(button);
            button.setOnAction((event)-> Client.getInstance().findProfileById(button.getId()));
            this.findAutocompleteBox.getChildren().add(hBox);
        }));
    }

    public void setContactInterface(ContactControllerInterface contactInterface) {
        this.contactInterface = contactInterface;
    }

    public void setContactList(ArrayList<User> contactList) {
        if (contactList.isEmpty()) {
            contactsListHide.toFront();
        }
        else {
            scrollPaneContacts.toFront();
            contactList.forEach(this::showContact);
        }
    }

    @Override
    public void setMainController(MainViewController mainViewController) {
        mainViewController.setContactsController(this);
    }

    private void showContact(User user){
        this.contactsField.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/contacts/contact.fxml"));
            HBox contact = loader.load();
            this.contactInterface = loader.getController();
            this.contactInterface.fillData(user);
            this.contactsField.getChildren().add(contact);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
