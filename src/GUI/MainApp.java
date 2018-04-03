package GUI;

import Utils.FSUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Optional;


import static javax.swing.text.html.HTML.Tag.I;

public class MainApp extends Application {
    private Stage primaryStage;
    private MainAppController controller;
    Pane root;
    private boolean hiddenFirstTime = true;
    private boolean isHidden = true;


    @Override
    public void stop() throws Exception {
        super.stop();
        if(controller.getDownloadService()==null) {
        } else {
            controller.terminateDownloads();
        }
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initLayout();
    }

    private void initLayout() {
        primaryStage.setTitle("Smart Trading Build Updater");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SMScanner.fxml"));
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
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("res\\GetBuildsIcon.png")));
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(we -> {
            primaryStage.hide();
            if (SystemTray.isSupported()) addAppToTray(hiddenFirstTime);
            hiddenFirstTime=false;
            isHidden=true;
            we.consume();
        });
        primaryStage.show();
    }

    private void addAppToTray(boolean hiddenFirstTime) {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                image = ImageIO.read(this.getClass().getResourceAsStream("res\\GetBuildsIcon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            MenuItem menuItemQuit = new MenuItem("Quit");
            menuItemQuit.addActionListener(e -> {
                promptConfirmation();
            });
            MenuItem menuItemRestore= new MenuItem("Restore");
            menuItemRestore.addActionListener(e -> {
                if (isHidden) Platform.runLater(() -> primaryStage.show());
                tray.remove(tray.getTrayIcons()[0]);
            });
            PopupMenu pMenu = new PopupMenu();
            pMenu.add(menuItemRestore);
            pMenu.add(menuItemQuit);
            TrayIcon trayIcon = new TrayIcon(image, "test", pMenu );
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            if(hiddenFirstTime) {
                trayIcon.displayMessage("Daemon", "Keeps working in background", TrayIcon.MessageType.INFO);
            }
        }

    private void promptConfirmation() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Quit?");
                alert.setHeaderText("Do you want to quit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Platform.exit();
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
