package GUI;

import Utils.FSUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

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
            System.exit(0);
        } else {
            controller.admitAndDisappear();
        }
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


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                primaryStage.hide();

                addAppToTray(hiddenFirstTime);
                hiddenFirstTime=false;
                isHidden=true;
                we.consume();
            }
        });

        primaryStage.show();
    }

    private void addAppToTray(boolean hiddenFirstTime) {
        if(SystemTray.isSupported()) {
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                image = ImageIO.read(this.getClass().getResourceAsStream("res\\GetBuildsIcon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            MenuItem menuItems[] = new MenuItem[2];
            menuItems[0] = new MenuItem("Quit");
            menuItems[0].addActionListener(e -> {
                Platform.exit();
            });
            menuItems[1] = new MenuItem("Restore");
            menuItems[1].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (isHidden) Platform.runLater(() -> primaryStage.show());
                    tray.remove(tray.getTrayIcons()[0]);
                }
            });
            PopupMenu pMenu = new PopupMenu();
            pMenu.add(menuItems[0]);
            pMenu.add(menuItems[1]);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "test", pMenu );
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            if(hiddenFirstTime) {
                trayIcon.displayMessage("Daemon", "Keeps working in background", TrayIcon.MessageType.INFO);
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


}
