package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextArea;
import netUtils.PullTask;
import netUtils.Scheduler;

/**
 * Created by c-consalpa on 2/1/2018.
 */
public class MainAppController {
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private Button startBtn;

    @FXML
    private TextArea consoleTextArea;

    @FXML
    private void initialize() {
        System.out.println("t");

    }

    @FXML
    private void startPolling() {
        Scheduler scheduler = new Scheduler(new String[]{"XEServer"}, "9.0.0", 10000);

        System.out.println("qqq");
    }



}
