package GUI;

import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableIntegerArray;
import javafx.collections.ObservableList;
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
    private File dst;
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
    private void onStartBtn(ActionEvent ev) {
        if (getProducts().size()==0) {
            return;
        }
        String[] selectedProducts = getProducts().toArray(new String[0]);
        String version = getVersion();
        int pollInterval = getPollInterval();
        String destinationDirectory = getDestinationDirectory();
//        System.out.println(getProducts());
//        System.out.println(version);
//        System.out.println(pollInterval);
//        System.out.println(destinationDirectory);
        new Scheduler(selectedProducts, version, 10000, this);
    }

    @FXML
    private void onBrowseBtn(ActionEvent ev) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Download builds to..");
        File destinationDirectory = chooser.showDialog(splitPane.getScene().getWindow());
        if (null!=destinationDirectoryTextField) {
            destinationDirectoryTextField.setText(destinationDirectory.toString() + "   ");
            dst = destinationDirectory;
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

    private String getDestinationDirectory() {
        return dst!=null?dst.toString():"D://Builds";
    }

    public TextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    public void consoleLog(String s) {
        consoleTextArea.appendText(s);
        consoleTextArea.appendText("\r\n");
    }
}
