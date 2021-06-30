package controllers.included;

import client.App;
import client.Client;
import client.models.FetchedCertificate;
import controllers.MainViewController;
import controllers.included.devices.DeviceController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class DevicesController implements ControllerInterface {
    public VBox devicesBox;
    public Label devicesCount;
    public Label userNameLabel;
    private int qty;
    @FXML
    public void initialize(){
        this.qty = 0;
        Client.getInstance().loadDevicesList();
        this.userNameLabel.setText(Client.getInstance().getUser().getFullName());
    }
    @Override
    public void setMainController(MainViewController mainViewController) {
        mainViewController.setDevicesController(this);
    }

    public void renderDevicesInfo(ArrayList<FetchedCertificate> arr){
        this.qty = 0;
        this.devicesBox.getChildren().clear();
        arr.forEach((el)->{
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/devices/device.fxml"));
            HBox hBox = null;
            try {
                hBox = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DeviceController deviceController = loader.getController();
            this.qty++;
            deviceController.fillData(this.qty, el.getDevice_name(), el.getLast_use(), !el.getBlocked(), el.getId());
            this.devicesBox.getChildren().add(hBox);
        });
        this.devicesCount.setText(String.valueOf(this.qty));
    }
}
