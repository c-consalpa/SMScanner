import GUI.MainAppController;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;


public class MainApp extends Application {
    private Stage primaryStage;
    private MainAppController controller;
    AnchorPane root;
    private boolean isHidden = true;


    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.terminateAndQuit();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initLayout();
        primaryStage.show();
    }

    private void initLayout() {
        primaryStage.setTitle("Build Puller");
        FXMLLoader loader = new FXMLLoader();
        try {
            URL loc = new URL(this.getClass().getResource("GUI/res/SM_Scanner.fxml").toExternalForm());
            System.out.println(loc);
            loader.setLocation(loc);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        loader.setLocation(getClass().getResource("GUI\\res\\SM_Scanner.fxml"));
        try {
            this.root = loader.load();
        } catch (IOException e) {
            System.out.println("Can't load FXML template.");
            e.printStackTrace();
        }
        controller = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("GUI/res/GetBuildsIcon.png")));
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(we -> {
            primaryStage.hide();
            if (SystemTray.isSupported() && controller.taskIsActive()) {
                String currentlyDownloadingBuildName = controller.getCurrentlyDownloadingBuild();
                addAppToTray(currentlyDownloadingBuildName);

                isHidden=true;
                we.consume();
            } else {
                Platform.exit();
            }

        });
        primaryStage.show();
    }

    private void addAppToTray(String currentlyDownloadingBuildName) {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                image = ImageIO.read(this.getClass().getResourceAsStream("GUI/res/GetBuildsIcon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            MenuItem menuItemQuit = new MenuItem("Quit");
            menuItemQuit.addActionListener(e -> {
                promptConfirmation(currentlyDownloadingBuildName);
            });
            MenuItem menuItemRestore= new MenuItem("Restore");
            menuItemRestore.addActionListener(e -> {
                if (isHidden) Platform.runLater(() -> primaryStage.show());
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
                    controller.terminateAndQuit();
                } else {
                    return;
                }
            }
        });

    }

    public static void main(String[] args) {
        Application.launch(args);
    }


}
