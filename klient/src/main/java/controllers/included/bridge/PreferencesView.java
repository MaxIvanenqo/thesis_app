package controllers.included.bridge;

import client.App;
import javafx.fxml.FXMLLoader;

public class PreferencesView implements IncludedView{
    @Override
    public FXMLLoader getLoader() {
        return new FXMLLoader(App.class.getResource("view/preferences.fxml"));
    }
}
