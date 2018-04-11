package GUI;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class MNotification {
    @FXML
    private ImageView imageView;

    @FXML
    private Text nproductName;

    @FXML
    private Text nproductVersion;

    @FXML
    private Text openProductLink;

    private final String productName, buildNumber;
    private final Stage notificationStage;
    private final File dir2showInExplorer;

    public MNotification(String pName, String bNumber, File dir2show) {
        productName = pName;
        buildNumber = bNumber;
        dir2showInExplorer = dir2show;

        notificationStage = new Stage(StageStyle.TRANSPARENT);
        notificationStage.setAlwaysOnTop(true);
        notificationStage.sizeToScene();
    }

    public void show () throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("\\res\\notification.fxml"));
        fxmlLoader.setController(this);
        AnchorPane root = fxmlLoader.load();
        nproductName.setText(productName);
        nproductVersion.setText(buildNumber);
//        image doesn't load  bitch
        Image quitImg = new Image("GUI/res/Close-Window-icon.png");
        imageView.setImage(quitImg);
        imageView.setOnMouseClicked(event ->
                closeStageAnimated(notificationStage));
        openProductLink.setOnMouseClicked(event ->
        {
            try {
                showBuildInExplorer(dir2showInExplorer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        final Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource(
                "res\\notification.css").toExternalForm());

        notificationStage.setScene(scene);
        positionNotificationStage(notificationStage);

        notificationStage.show();
        //shifting root element +300px leftwards
        root.setLayoutX(+ 400d);

        //Stage life duration
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished( event ->
                closeStageAnimated(notificationStage)

        );
        delay.play();

        //popup effect
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), root);
        translateTransition.setByX(-402d);
        translateTransition.play();

    }

    private void showBuildInExplorer(File dir) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(dir);
    }

    private void closeStageAnimated(Stage notificationStage) {
        Node root2Hide = notificationStage.getScene().getRoot();
        SequentialTransition sequentialTransition = new SequentialTransition();
        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(150), root2Hide);
        translateTransition1.setByX(-15);

        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(300), root2Hide);
        translateTransition2.setByX(500);

        sequentialTransition.getChildren().add(translateTransition1);
        sequentialTransition.getChildren().add(translateTransition2);
        sequentialTransition.setOnFinished(event -> notificationStage.close());
        sequentialTransition.play();
    }

    private void positionNotificationStage(Stage nStage) {
        double  screenWidth,
                screenHeight,
                notificationWidth = 400d,
                notificationHeight = 100d;

        Rectangle2D screenRect = Screen.getPrimary().getVisualBounds();
        screenWidth = screenRect.getWidth();
        screenHeight = screenRect.getHeight();
        nStage.setX(screenWidth - notificationWidth - 2);
        nStage.setY(screenHeight - notificationHeight - 2);
    }

}
