package controllers.included.devices;

import client.App;
import client.Client;
import client.utils.DialogUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class DeviceController {
    public ImageView blocked;
    public Label listNumber;
    public TextField deviceName;
    public Label lastUse;
    public Button toggle;
    private String id;
    private boolean status;

    public void fillData(int N, String deviceName, String lastUse, boolean blocked, String id){
        this.status = blocked;
        this.listNumber.setText(String.valueOf(N));
        this.blocked.setVisible(this.status);
        this.deviceName.setText(deviceName);
        this.lastUse.setText(lastUse);
        this.toggle.getStyleClass().add(this.status?"on":"off");
        this.id = id;
    }

    public void statusControl() {
        if (this.status){
            this.toggle.getStyleClass().remove("on");
            this.toggle.getStyleClass().add("off");
            this.blocked.setVisible(false);
        } else {
            this.toggle.getStyleClass().remove("off");
            this.toggle.getStyleClass().add("on");
            this.blocked.setVisible(true);
        }
        this.status = !this.status;
    }

    public void toggleAccessibility() {
        if (DialogUtil.confirmationAlert("Zablokować dostęp dla " +
                deviceName.getText() + " (ostatnio używany " + lastUse.getText() +") ? \nOdblokować można tylko używając innego zarejestrowanego urządzenia. " +
                "Jeśli blokujesz aktywnie podłączone urządzenie spowoduje to jego natychmiastowe wylogowanie z systemu")){
            this.statusControl();
            Client.getInstance().changeCertificateStatus(this.id);
        }
    }
}
