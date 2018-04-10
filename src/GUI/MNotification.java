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

import java.io.IOException;


public class MNotification {
    String productName, buildNumber;
    private final Stage notificationStage;

    public MNotification(String pName, String bNumber) {
        productName = pName;
        buildNumber = bNumber;
        notificationStage = new Stage(StageStyle.TRANSPARENT);
    }

    public void show () throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("\\res\\notification.fxml"));
        fxmlLoader.setController(this);
        AnchorPane root = fxmlLoader.load();
        nproductName.setText(productName);
        nproductVersion.setText(buildNumber);
//        image doesnt load itself bitch
        Image quitImg = new Image("GUI/res/Close-Window-icon.png");
        imageView.setImage(quitImg);
        imageView.setOnMouseClicked(event ->
                notificationStage.close());
        final Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("res\\notification.css").toExternalForm());


        notificationStage.setScene(scene);
        positionNotificationStage(notificationStage);
        notificationStage.show();
        //shifting root element +300px leftwards
        root.setLayoutX(400);

        //popup effect
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), root);
        translateTransition.setByX(-402d);
        translateTransition.play();

        //Stage life duration
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished( event ->
                closeStageAnimated(notificationStage)

        );
        delay.play();

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

    @FXML
    private ImageView imageView;

    @FXML
    private Text nproductName;

    @FXML
    private Text nproductVersion;
}
