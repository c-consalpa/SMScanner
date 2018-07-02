package GUI;

import Utils.Common;

import Utils.FSUtils;
import Work.DownloadService;
import Work.DownloadTask;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainAppController {
    private DownloadService downloadService;
    private boolean MainWindowisHidden = true;

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
    private ProgressBar overallProgressBar;

    @FXML ProgressBar singleProductProgresssBar;


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
    private GridPane progressRegion;


    @FXML
    private void onStartBtn(ActionEvent ev) {
        showProgressBars();

//        if (!validateUIFields()) return;
//        disableUI(true);
//
        String[] products = new String[getProducts().size()];
        System.arraycopy(getProducts().toArray(), 0, products, 0, getProducts().size());
        String version = getVersion();
        File destination = getDestination2DownloadFiles();
        int pollingInterval = getPollInterval();
        downloadService = new DownloadService(products, version, destination, this);
//        downloadService.setPeriod(Duration.hours(pollingInterval));
        downloadService.setPeriod(Duration.seconds(20));
        downloadService.start();
    }

    private void showProgressBars() {
        Scene scene = root.getScene();
        Stage stage = (Stage) scene.getWindow();
        double stageHeight = stage.heightProperty().doubleValue();
        SimpleDoubleProperty sceneHeight = new SimpleDoubleProperty();
        sceneHeight.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stage.setHeight(newValue.doubleValue());
            }
        });
        Timeline resizer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(sceneHeight, stageHeight)),
                new KeyFrame(Duration.millis(250), new KeyValue(sceneHeight, stageHeight + 35))
        );
        resizer.setOnFinished(event -> {
            progressRegion.setVisible(true);
        });
        resizer.setCycleCount(1);
        resizer.setAutoReverse(true);
        resizer.play();
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

    private File getDestination2DownloadFiles() {
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
        DownloadService runningService = getDownloadService();
        if (runningService != null && runningService.getState().equals(Worker.State.RUNNING)) {
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

    public void addAppToTray(String currentlyDownloadingBuildName, Stage primaryStage) {
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("res/GetBuildsIcon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        java.awt.MenuItem menuItemQuit = new java.awt.MenuItem("Quit");
        menuItemQuit.addActionListener(e -> {
            promptConfirmation(currentlyDownloadingBuildName);
        });
        java.awt.MenuItem menuItemRestore= new java.awt.MenuItem("Restore");
        menuItemRestore.addActionListener(e -> {
            if (MainWindowisHidden) Platform.runLater(() -> primaryStage.show());
            tray.remove(tray.getTrayIcons()[0]);
        });
        PopupMenu pMenu = new PopupMenu();
        pMenu.add(menuItemRestore);
        pMenu.add(menuItemQuit);
        TrayIcon trayIcon = new TrayIcon(image, "Currently downloading...", pMenu );
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        String trayNotificationMessage = "Build is currently downloading";
        if (currentlyDownloadingBuildName!=null) {
            trayNotificationMessage += (": " + currentlyDownloadingBuildName);
        }
        trayIcon.displayMessage("Running in daemon", trayNotificationMessage, TrayIcon.MessageType.INFO);
    }

    private void promptConfirmation(String currentlyDownloadingBuildName) {
        final String activeDownloadingBuildName = currentlyDownloadingBuildName;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Quit?");
                alert.setHeaderText("Do you really want to quit?");
                alert.setContentText("The following build is currently downloading: " + activeDownloadingBuildName);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    terminateAndQuit();
                } else {
                    return;
                }
            }
        });

    }

    public void bindOverallProgress(DownloadTask task) {
        Platform.runLater(() -> {
            overallProgressBar.progressProperty().bind(task.progressProperty());
        });
    }

    public void setMaxProgressAndWait(int max) {
        Platform.runLater(() -> {
            System.out.println("UNBIND");
            overallProgressBar.progressProperty().unbind();
            overallProgressBar.setProgress(max);
        });
    }

    public void setOverallProgress(int i) {
        overallProgressBar.setProgress(0);
    }

    public void updateSingleProductProgress(double step) {
        Platform.runLater(() -> {
            singleProductProgresssBar.setProgress(step);
        });

    }
}
