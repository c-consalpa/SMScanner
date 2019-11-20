package GUI;

import Utils.Common;
import Work.DownloadService;
import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
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
    private boolean MainWindowisHidden = false;

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
    private GridPane progressRegion;

    @FXML
    private ProgressBar totalProgressBar;

    @FXML
    private Text downloadingProductLabel;

    @FXML
    private ProgressBar singleProductProgresssBar;

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
            nullifyProgress();
        }
        disableUI(false);
    }

    @FXML
    private void onStartBtn(ActionEvent ev) {
        if (!validateUIFields()) return;
        disableUI(true);

        String[] products = new String[getProductsFromCheckboxes().size()];
        System.arraycopy(getProductsFromCheckboxes().toArray(), 0, products, 0, getProductsFromCheckboxes().size());
        String version = getVersion();
        File destination = getDestination2DownloadFiles();
        int pollingInterval = getPollInterval();
        downloadService = new DownloadService(products, version, destination, this);
        downloadService.setPeriod(Duration.hours(pollingInterval));
//        downloadService.setPeriod(Duration.seconds(20));
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
        if (getProductsFromCheckboxes().size() < 1) {
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
        String[] versionsArr = new String[]{"8.6.0", "8.8.0", "8.8.1", "9.0.0", "9.0.1", "9.1.0", "9.1.1", "9.2.0"};
        ObservableList<String> versionObsList = FXCollections.observableArrayList(versionsArr);
        choice_version.setItems(versionObsList);
//        last array elem is default
        choice_version.getSelectionModel().select(versionObsList.size() - 1);
    }

    private void setupPollChoiceList() {
        Integer[] intervalArr = new Integer[]{3, 6, 9, 12, 24, 48};
        ObservableList pollIntervalList = FXCollections.observableArrayList(intervalArr);
        choice_poll.setItems(pollIntervalList);
        choice_poll.getSelectionModel().select(pollIntervalList.indexOf(24));
    }

    private List<String> getProductsFromCheckboxes() {
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

    public boolean isDownloadingNow() {
        if (downloadService == null) return false;

        Worker.State cycleState = downloadService.getState();
        if (cycleState.equals(Worker.State.RUNNING)) {
            return true;
        } else {
            return false;
        }
    }


    public void getState() {
        if (downloadService == null) {
            System.out.println("donwload service is null");
        } else {
            System.out.println(downloadService.getState());
        }

    }

    public void addAppToTray(Stage primaryStage) {
        String currentArtifactName = getCurrentArtifactName();
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("res/GetBuildsIcon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        java.awt.MenuItem menuItem_quit = new java.awt.MenuItem("Quit");
        menuItem_quit.addActionListener(e ->
            prompTrayQuitConfirmation()
        );
        java.awt.MenuItem menuItem_restore = new java.awt.MenuItem("Restore");
        menuItem_restore.addActionListener(e -> {
            if (MainWindowisHidden) {
                Platform.runLater(() -> primaryStage.show());
                tray.remove(tray.getTrayIcons()[0]);
                MainWindowisHidden = false;
            }
        });

        PopupMenu trayRClickMenu = new PopupMenu();
        trayRClickMenu.add(menuItem_restore);
        trayRClickMenu.add(menuItem_quit);
        TrayIcon trayIcon = new TrayIcon(image, "Currently downloading...", trayRClickMenu );
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        String trayNotificationMessage = "Idle. Next download in: { // TODO}";


        if (isDownloadingNow()) {
            trayNotificationMessage = ("Build is currently downloading: " + currentArtifactName);
        }
        trayIcon.displayMessage("Running in background..", trayNotificationMessage, TrayIcon.MessageType.INFO);
        MainWindowisHidden = true;
    }

    public String getCurrentArtifactName() {
        if (isDownloadingNow()) {
            return downloadService.currentTask.getCurrentBuildName();
        } else {
            return null;
        }
    }

    private void prompTrayQuitConfirmation() {
        System.out.println("prompTrayQuitConfirmation");
        String currentlyDownloadingBuildName;
        if (downloadService != null && downloadService.currentTask != null) {
            currentlyDownloadingBuildName = downloadService.currentTask.getCurrentBuildName();
        } else {
            currentlyDownloadingBuildName = null;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                String alertContextMsg;
                if (currentlyDownloadingBuildName != null) {
                    alertContextMsg =   "The following build is currently downloading: "
                                        + currentlyDownloadingBuildName
                                        + ";\r\n"
                                        + "queued builds: " + "buildsLeftNum();";
                } else {
                    alertContextMsg = "No builds are downloaded at the moment;";
                }

                alert.setTitle("Quit?");
                alert.setHeaderText("Do you really want to quit?");
                alert.setContentText(alertContextMsg);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    terminateAndQuit();
                } else {
                    return;
                }
            }
        });

    }

    public void setOverallProgress(double i) {
        Platform.runLater( () ->
            totalProgressBar.setProgress(i));
    }

    public void setIndividualProgress(double step) {
        Platform.runLater(() -> {
            singleProductProgresssBar.setProgress(step);
        });
    }

    public void nullifyProgress() {
        Platform.runLater(() -> {
        singleProductProgresssBar.setProgress(0d);
        totalProgressBar.setProgress(0d);
        downloadingProductLabel.setText("Product");
        });
    }

    public void setCurrentArtifactName(String product) {
        final int Ellipsis_threshold = 10;

        Platform.runLater(() -> {
            String productName = product;
            if (product.length() > Ellipsis_threshold) {
                productName = product.substring(0, Ellipsis_threshold);
                productName += "...";
            }
            downloadingProductLabel.setText(productName);

        });
    }
}
