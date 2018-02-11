package GUI;

import FSUtils.FSUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import netUtils.netBrowser;

import java.io.IOException;


public class MainApp extends Application {
    private Stage primaryStage;
    private MainAppController controller;
    SplitPane root;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        initLayout();
    }

    private void initLayout() throws IOException {
        primaryStage.setTitle("Hello World");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SMScanner.fxml"));
        this.root = loader.load();
        controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        System.out.println("MainApp: "+Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
