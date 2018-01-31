package GUI; /**
 * Created by c-consalpa on 1/31/2018.
 */

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    @FXML TextArea textArea;

    private void f() {
        textArea.setText("Test");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("res/layout.fxml"));
        primaryStage.setTitle("SMScanner");






        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }




}
