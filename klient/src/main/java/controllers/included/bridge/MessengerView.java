package controllers.included.bridge;

import client.App;
import javafx.fxml.FXMLLoader;

public class MessengerView implements IncludedView{
    @Override
    public FXMLLoader getLoader() {
        return new FXMLLoader(App.class.getResource("view/messenger.fxml"));
    }
}
