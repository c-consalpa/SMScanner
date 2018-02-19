package GUI;

import Utils.Common;
import Utils.FSUtils;
import Work.DownloadService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by c-consalpa on 2/1/2018.
 */
public class MainAppController {
    private MainApp mainApp;
    private DownloadService downloadService;

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
    private Button startBtn;

    @FXML
    private void initialize() {
        setupVersionChoiceList();
        setupPollChoiceList();
    }

    @FXML
    private void onStop(ActionEvent ev) {
        downloadService.cancel();
        startBtn.setDisable(false);
    }



    @FXML
    private void onStartBtn(ActionEvent ev) {
        disableUI(splitPane);
        String[] products = new String[getProducts().size()];
        System.arraycopy(getProducts().toArray(), 0, products, 0, getProducts().size());
        String version = getVersion();
        int pollingInterval = getPollInterval()*60*60*1000;
        File destination = getDestination();

        downloadService = new DownloadService(products, version, pollingInterval, destination, this);
        downloadService.setPeriod(new Duration(30000));
        downloadService.start();
    }

    private void disableUI(Parent n) {
        for (Node target:
             n.getChildrenUnmodifiable()) {
            System.out.println(target.toString());
            target.ge
        }
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
            Utils.Common.HOMEFS_BUILDS_FOLDER = destinationDirectory.toString()+"\\Builds\\";
        } else {
            destinationDirectoryTextField.setText(Utils.Common.HOMEFS_BUILDS_FOLDER);
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
                // Getting product name by "prod_..." fx_IDs
                l.add(((CheckBox) node).getId().substring(5));
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
            destination = new File(Utils.Common.HOMEFS_BUILDS_FOLDER);
        } else {
            Common.HOMEFS_BUILDS_FOLDER = tmp+"\\Builds\\";
            destination = new File(tmp);
        }
        return destination;
    }

    public void consoleLog(String s) {
        Platform.runLater(() -> {
            consoleTextArea.appendText(s);
            consoleTextArea.appendText("\r\n");
        });
    }
}
