package GUI;

import FSUtils.FSUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import netUtils.netBrowser;

import java.io.IOException;


public class MainApp extends Application {
    private Stage primaryStage;
    private MainAppController controller;
    AnchorPane root;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        initLayout();
    }

    private void initLayout() throws IOException {
        primaryStage.setTitle("Hello World");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainScreen.fxml"));
        this.root = loader.load();
        controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();

    }


}
