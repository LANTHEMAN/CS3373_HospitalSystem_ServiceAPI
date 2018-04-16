package edu.wpi.cs3733d18.teamF.api.controller.page;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733d18.teamF.api.controller.PaneSwitcher;
import edu.wpi.cs3733d18.teamF.api.controller.SwitchableController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class MainPage implements SwitchableController {
    PaneSwitcher switcher;
    @FXML
    AnchorPane languageInterpreterPane;
    @FXML
    JFXTextField languageField;
    @FXML
    JFXTextField firstNameLanguage;
    @FXML
    JFXTextField lastNameLanguage;
    @FXML
    JFXTextField destinationLanguage;
    @FXML
    JFXTextArea instructionsLanguage;
    @FXML
    Label languageRequiredLI;
    @FXML
    Label lastNameRequiredLI;
    @FXML
    Label firstNameRequiredLI;
    @FXML
    Label locationRequiredLI;
    @FXML
    ToggleGroup securityToggle;
    @FXML
    JFXTextField securityLocationField;
    @FXML
    JFXTextArea securityTextArea;
    @FXML
    AnchorPane securityPane;

    @FXML
    JFXButton selectLanguage;
    @FXML
    JFXButton selectSecurity;
    @FXML
    GridPane serviceRequestPane;

    @Override
    public void initialize(PaneSwitcher switcher) {
        this.switcher = switcher;
    }

    void onLanguage(){
        languageInterpreterPane.setVisible(true);
        securityPane.setVisible(false);
        selectLanguage.setVisible(false);
        selectSecurity.setVisible(false);
    }

    void onSecurity(){
        languageInterpreterPane.setVisible(false);
        securityPane.setVisible(true);
        selectLanguage.setVisible(false);
        selectSecurity.setVisible(false);
    }

    void setGridPaneDimensions(int windowWidth, int windowLength){
        serviceRequestPane.setPrefSize(windowWidth, windowLength);
    }
}
