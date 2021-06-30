package controllers.included;

import controllers.MainViewController;
import javafx.fxml.FXML;

public class AboutController implements ControllerInterface {
    private MainViewController mainViewController;
    @FXML
    public void initialize(){
    }

    @Override
    public void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
