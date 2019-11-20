import GUI.MainAppController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;


public class MainApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    private Stage primaryStage;
    private MainAppController controller;
    AnchorPane root;

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
        loader.setLocation(this.getClass().getResource("GUI/res/SM_Scanner.fxml"));
        try {
            this.root = loader.load();
        } catch (IOException e) {
            System.out.println("Can't load FXML template.");
            e.printStackTrace();
        }
        controller = loader.getController();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
//        primaryStage.setHeight(392);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("GUI/res/GetBuildsIcon.png")));
//        primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("GUI/res/GetBuildsIcon.png")));
        Platform.setImplicitExit(false);
        preventClosingIfRunning(primaryStage);

    }

    private void preventClosingIfRunning(Stage stage) {

        stage.setOnCloseRequest(we -> {
            boolean cycleStatus = controller.cycleIsActive();
            stage.hide();
            if (SystemTray.isSupported()) {
                controller.addAppToTray(stage);
                we.consume();
            } else {
                Platform.exit();
            }
        });
    }

}
