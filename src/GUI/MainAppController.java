package GUI;

import FSUtils.FSUtil;
import GUI.MainApp;
import Work.DownloadService;
import Work.downloadTask;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import netUtils.PullTask;
import netUtils.Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by c-consalpa on 2/1/2018.
 */
public class MainAppController {

    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private SplitPane splitPane;

    @FXML
    private GridPane prod_checkBoxes;

    @FXML
    private ChoiceBox<String> choice_version;

    @FXML
    private ChoiceBox<Integer> choice_poll;

    @FXML
    private TextField destinationDirectoryTextField;

    @FXML
    private TextArea consoleTextArea;

    @FXML
    private void initialize() {
        setupVersionChoiceList();
        setupPollChoiceList();
    }

    @FXML
    private void onStop(ActionEvent ev) {

    }
    DownloadService downloadService;
    @FXML
    private void onStartBtn(ActionEvent ev) {
        String[] products = new String[getProducts().size()];
        System.arraycopy(getProducts().toArray(), 0, products, 0, getProducts().size());
        String version = getVersion();
        int pollingInterval = getPollInterval();
        File destination = getDestination();


        downloadService = new DownloadService(products, version, pollingInterval, destination, this);
        downloadService.start();

    }

    @FXML
    private void onBrowseBtn(ActionEvent ev) {
        setDestinationField();
    }


    private void setDestinationField() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Download builds to..");
        File destinationDirectory = chooser.showDialog(splitPane.getScene().getWindow());
        if (destinationDirectory!=null) {
            destinationDirectoryTextField.setText(destinationDirectory.toString());
            FSUtil.HOMEFS_BUILDS_FOLDER = destinationDirectory.toString()+"\\Builds\\";
        } else {
            destinationDirectoryTextField.setText(FSUtil.HOMEFS_BUILDS_FOLDER);
        }
        destinationDirectoryTextField.positionCaret(100);
    }

    private void setupVersionChoiceList() {
        String[] versionsArr = new String[]{"8.6.0", "8.8.0", "8.8.1", "9.0.0"};
        ObservableList<String> versionObsList = FXCollections.observableArrayList(versionsArr);
        choice_version.setItems(versionObsList);
        choice_version.getSelectionModel().select(versionObsList.size()-1);
    }

    private void setupPollChoiceList() {
        Integer[] intervalArr = new Integer[] {3, 6, 9, 12, 24, 48};
        ObservableList pollIntervalList = FXCollections.observableArrayList(intervalArr);
        choice_poll.setItems(pollIntervalList);
        choice_poll.getSelectionModel().select(pollIntervalList.indexOf(24));
    }

    private List<String> getProducts() {
        List<String> l = new ArrayList<>(5);
        for (Object node:
                prod_checkBoxes.getChildren()) {
            if  (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                l.add(((CheckBox) node).getText());
            }
        }
        return l;
    }

    private String getVersion() {
        return choice_version.getValue();
    }

    private int getPollInterval() {
        return choice_poll.getValue();
    }

    private File getDestination() {
        String tmp = destinationDirectoryTextField.getText();
        File destination;
        if (tmp.isEmpty()) {
            destination = new File(FSUtil.HOMEFS_BUILDS_FOLDER);
        } else {
            FSUtil.HOMEFS_BUILDS_FOLDER = tmp+"\\Builds\\";
            destination = new File(tmp);
        }
        return destination;
    }

    public TextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    public void consoleLog(String s) {
        consoleTextArea.appendText(s);
        consoleTextArea.appendText("\r\n");
    }
}
