package GUI;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainScreen extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("SMScanner");
        primaryStage.setWidth(500d);
        primaryStage.setHeight(500d);
        GridPane root = new GridPane();
        VBox leftBox = new VBox(10);
        VBox rightBox = new VBox(10);
        HBox hBox = new HBox(10);


        leftBox.setPadding(new Insets(50, 10, 10, 10));

        Label label = new Label("Select products:");
        label.setFont(new Font("verdana", 24));

        CheckBox eamCheckBox = new CheckBox("EAM");
        CheckBox xesCheckBox = new CheckBox("XEServer");
        CheckBox xesmCheckBox = new CheckBox("XEServer Manager");
        CheckBox xecCheckBox = new CheckBox("XEConnect");
        CheckBox smCheckBox = new CheckBox("State Management");

        leftBox.getChildren().addAll(label, eamCheckBox, xesCheckBox, xesmCheckBox, xecCheckBox, smCheckBox);

        Label versionlabel = new Label("Select Version:");
        versionlabel.setFont(new Font("verdana", 24));

        ChoiceBox versionBox = new ChoiceBox();
        versionBox.getItems().addAll(new String[]{"9.0.0", "8.8.0", "8.8.1", "8.6.0"});
        versionBox.getSelectionModel().selectFirst();


        rightBox.setPadding(new Insets(50, 10, 10, 10));
        Label pollingIntervalLabel = new Label("Interval (hours):");
        ChoiceBox<Integer> pollingIntervalBox = new ChoiceBox();

        pollingIntervalBox.getItems().addAll(new Integer[]{24, 36, 48, 60, 72});
        pollingIntervalBox.getSelectionModel().selectFirst();




        Button openFileButton = new Button("Open File");
        openFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.showOpenDialog(primaryStage);
            }
        });

        rightBox.getChildren().addAll(versionlabel, versionBox, pollingIntervalLabel, pollingIntervalBox);
        root.add(leftBox, 0, 0);
        root.add(rightBox, 1, 0);
        root.add(openFileButton, 0,2);





        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
