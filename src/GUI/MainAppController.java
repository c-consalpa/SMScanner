package GUI;

import Utils.Common;

import Work.DownloadService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainAppController {
    private DownloadService downloadService;

    public DownloadService getDownloadService() {
        return downloadService;
    }

    @FXML
    private AnchorPane root;


    @FXML
    private GridPane prod_checkBoxes;

    @FXML
    private GridPane rightGrid;

    @FXML
    private HBox pathBar;

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
    private void clearConsole() {
        consoleTextArea.clear();
    }

    @FXML
    private void onStop(ActionEvent ev) {
        if (downloadService != null) {
            downloadService.cancel();
        }
        disableUI(false);
    }

    @FXML
    private void onStartBtn(ActionEvent ev) {
        if (!validateUIFields()) return;
        disableUI(true);

        String[] products = new String[getProducts().size()];
        System.arraycopy(getProducts().toArray(), 0, products, 0, getProducts().size());
        String version = getVersion();
        File destination = getDestination();
        int pollingInterval = getPollInterval();
        downloadService = new DownloadService(products, version, destination, this);
        downloadService.setPeriod(Duration.hours(pollingInterval));
        downloadService.start();
    }

    @FXML
    private void onBrowseBtn(ActionEvent ev) {
        setDestinationField();
    }

    private void disableUI(Boolean b) {
        prod_checkBoxes.setDisable(b);
        rightGrid.setDisable(b);
        pathBar.setDisable(b);
        startBtn.setDisable(b);
    }

    private boolean validateUIFields() {
        if (getProducts().size() < 1) {
            blinkElement(prod_checkBoxes);
            return false;
        } else if (destinationDirectoryTextField.getText().isEmpty()) {
            blinkElement(pathBar);
            return false;
        } else return true;
    }

    private void blinkElement(Node n) {
        //Get abs coords:
        Bounds boundsInScene = n.localToScene(n.getBoundsInLocal());
        double rectWidth = boundsInScene.getWidth() + 10;
        double rectHeight = boundsInScene.getHeight() + 10;
        double rectX = boundsInScene.getMinX() - 5;
        double rectY = boundsInScene.getMinY() - 5;

        Rectangle rectangle = new Rectangle(rectWidth, rectHeight);
        rectangle.setX(rectX);
        rectangle.setY(rectY);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.RED);
        rectangle.setMouseTransparent(true);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.1), rectangle);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(4);
        fadeTransition.play();

        root.getChildren().add(rectangle);
    }

    private void setDestinationField() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Download builds to..");
        File destinationDirectory = chooser.showDialog(root.getScene().getWindow());
        if (destinationDirectory != null) {
            destinationDirectoryTextField.setText(destinationDirectory.toString());
            Utils.Common.HOMEFS_BUILDS_FOLDER = destinationDirectory.toString() + "\\Builds\\";
        }
        destinationDirectoryTextField.positionCaret(100);
    }

    private void setupVersionChoiceList() {
        String[] versionsArr = new String[]{"8.6.0", "8.8.0", "8.8.1", "9.0.0"};
        ObservableList<String> versionObsList = FXCollections.observableArrayList(versionsArr);
        choice_version.setItems(versionObsList);
        choice_version.getSelectionModel().select(versionObsList.size() - 1);
    }

    private void setupPollChoiceList() {
        Integer[] intervalArr = new Integer[]{3, 6, 9, 12, 24, 48};
        ObservableList pollIntervalList = FXCollections.observableArrayList(intervalArr);
        choice_poll.setItems(pollIntervalList);
        choice_poll.getSelectionModel().select(pollIntervalList.indexOf(24));
    }

    private List<String> getProducts() {
        List<String> l = new ArrayList<>(5);
        for (Object node :
                prod_checkBoxes.getChildren()) {
            if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
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
            Common.HOMEFS_BUILDS_FOLDER = tmp + "\\Builds\\";
            destination = new File(tmp);
        }
        return destination;
    }

    public synchronized void consoleLog(String s) {
        consoleTextArea.appendText(s);
        consoleTextArea.appendText("\r\n");
    }

    public void terminateAndQuit() {
        if (downloadService == null) System.exit(0);
        if (downloadService.getState().equals(Worker.State.RUNNING)) {
            downloadService.cancel();
//            giving time to cleanup non-finished builds;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public boolean taskIsActive() {
        DownloadService activeService = getDownloadService();
        if (activeService != null && activeService.getState().equals(Worker.State.RUNNING)) {
            return true;
        }
        return false;
    }

    public String getCurrentlyDownloadingBuild() {
        if (downloadService!=null) {
            return downloadService.currentTask.getCurrentlyDownloadedBuild();
        } else {
            return null;
        }
    }
}
