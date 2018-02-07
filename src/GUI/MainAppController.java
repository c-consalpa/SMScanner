package GUI;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import netUtils.PullTask;
import netUtils.Scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by c-consalpa on 2/1/2018.
 */
public class MainAppController {
    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private CheckBox prod_EAM;

    @FXML
    private CheckBox prod_XES;

    @FXML
    private CheckBox prod_XEC;

    @FXML
    private CheckBox prod_SM;

    @FXML
    private CheckBox prod;

    @FXML
    private GridPane prod_checkBoxes;

    @FXML
    private ChoiceBox<String> choice_version;


    @FXML
    private void initialize() {
        setupChoiceList();
    }

    @FXML
    private void onStartBtn(ActionEvent ev) {
        if (getProducts().size()==0) {
            return;
        }
        System.out.println(getVersion());
        System.out.println(getProducts());
    }


    private void setupChoiceList() {
        choice_version.setItems(FXCollections.observableArrayList("a", "b"));
    }

    private List<String> getProducts() {
        List<String> l = new ArrayList<>(5);
        for (Object node:
                prod_checkBoxes.getChildren()) {
            if  (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                l.add(((CheckBox) node).getText());
            }
        }
        return l;
    }

    private String getVersion() {
        return choice_version.getValue();
    }
}
