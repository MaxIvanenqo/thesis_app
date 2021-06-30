package client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.concurrent.atomic.AtomicBoolean;

public class DialogUtil {
    public static void dialogError(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd!");
        alert.setHeaderText("Operacja nie powiodła się");
        alert.setContentText(text);
        alert.showAndWait();
        alert.getDialogPane().setMinHeight(300.0);
    }

    public static void warningAlert(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Uwaga!");
        alert.setHeaderText("Informacja od systemu");
        alert.setContentText(text);
        alert.show();
        alert.getDialogPane().setMinHeight(300.0);
    }

    public static boolean confirmationAlert(String str){
        ButtonType YES = new ButtonType("Tak");
        ButtonType NO = new ButtonType("Nie");
        Alert alert = new Alert(Alert.AlertType.NONE, "Opcje:", YES, NO);
        alert.getDialogPane().setMinHeight(250.0);
        alert.getDialogPane().setMinWidth(350.0);
        alert.setTitle("Okno potwierdzenia");
        alert.setHeaderText("Potwierdź operacje");
        alert.setContentText(str);
        AtomicBoolean res = new AtomicBoolean(false);
        alert.showAndWait().ifPresent(response -> {
            if (response == YES) {
                res.set(true);
            } else if (response == NO) {
                res.set(false);
            }
        });
        return res.get();
    }
}
