package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Client.getInstance();
        AnchorPane root = FXMLLoader.load(App.class.getResource("view/authorization.fxml"));
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    @Override
    public void stop(){
        Client.getInstance().disconnectImmediately();
    }

    public static void main(String[] args) {
        Thread thread = new Thread(() -> launch(args));
        thread.start();
    }
}