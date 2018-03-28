package GUI;

import Utils.FSUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private Stage primaryStage;
    private MainAppController controller;
    Pane root;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initLayout();
    }

    private void initLayout()  {
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
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
