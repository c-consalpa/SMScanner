package GUI;

import Utils.Common;

import Work.DownloadService;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sun.imageio.plugins.jpeg.JPEG.version;

/**
 * Created by c-consalpa on 2/1/2018.
 */
public class MainAppController {

    private DownloadService downloadService;

    @FXML
    private Pane root;

    @FXML
    private SplitPane splitPane;

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
    private void onStop(ActionEvent ev) {
        if (downloadService!=null) {
            downloadService.cancel();
        }
        disableUI(false);
    }


    private void blinkElement(Node n) {
        double nodeWidth = n.getLayoutBounds().getWidth() + 10;
        double nodeHeight = n.getLayoutBounds().getHeight() + 10;
        double nodeX = n.getLayoutX() - 5;
        double nodeY = n.getLayoutY() - 5;
        Rectangle rectangle = new Rectangle(nodeWidth, nodeHeight);
        rectangle.setX(nodeX);
        rectangle.setY(nodeY);
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

    @FXML
    private void onStartBtn(ActionEvent ev) {
        boolean UIvalidationPassed = validateUIFields(); 
        if (!UIvalidationPassed) {
            return;
        }
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
        if(getProducts().size()<1) {
            blinkElement(prod_checkBoxes);
            return false;
        } else if (destinationDirectoryTextField.getText().isEmpty()) {
            blinkElement(pathBar);
            return false;
        } else return true;
    }

    private void setDestinationField() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Download builds to..");
        File destinationDirectory = chooser.showDialog(splitPane.getScene().getWindow());
        if (destinationDirectory!=null) {
            destinationDirectoryTextField.setText(destinationDirectory.toString());
            Utils.Common.HOMEFS_BUILDS_FOLDER = destinationDirectory.toString()+"\\Builds\\";
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

    public synchronized void consoleLog(String s) {
            consoleTextArea.appendText(s);
            consoleTextArea.appendText("\r\n");
    }
}
