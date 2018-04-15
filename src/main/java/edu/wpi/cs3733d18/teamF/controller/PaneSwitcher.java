package edu.wpi.cs3733d18.teamF.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class PaneSwitcher {
    private HashMap<String, Pane> panes;
    private Scene scene;
    private Stage popup = new Stage();

    public PaneSwitcher(Scene scene) {
        this.scene = scene;
        panes = new HashMap<>();

        popup.initModality(Modality.APPLICATION_MODAL);
    }

    public Scene getScene() {
        return scene;
    }

    public Pane getPane(Screens.Screen screen) {
        return panes.get(screen.fxmlFile);
    }

    // loads the screen into memory from the file
    public void load(Screens.Screen screen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.fxmlFile));
            Pane pane = loader.load();

            panes.put(screen.fxmlFile, pane);

            Object controller = loader.getController();

            SwitchableController switchableController = (SwitchableController) controller;
            switchableController.initialize(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // removes a screen entry from memory
    public void remove(Screens.Screen screen) {
        panes.remove(screen.fxmlFile);
    }

    // assumes the screen is already loaded
    public void setActive(Screens.Screen screen) {
        scene.setRoot(panes.get(screen.fxmlFile));
    }

    // clears all screens and load the given screen
    public void switchTo(Screens.Screen screen) {
        panes.clear();
        load(screen);
        scene.setRoot(panes.get(screen.fxmlFile));
    }

    public void switchResource(ResourceBundle resource, Screens.Screen screen) {
        // reload the given screen
        panes.clear();
        load(screen);
        scene.setRoot(panes.get(screen.fxmlFile));
    }

    public void popup(Screens.Screen screen) {
        load(screen);
        Scene popScene = new Scene(panes.get(screen.fxmlFile));
        popup.setScene(popScene);
        popup.show();
    }

    public void closePopup(Screens.Screen screen) {
        // will reset control back to the given screen (that should be displayed currently)
        popup.close();
        load(screen);
        scene.setRoot(panes.get(screen.fxmlFile));
    }

    public <Element> Pair<Element, Pane> loadElement(String fxmlFile){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("element/" + fxmlFile));
            Pane pane = loader.load();
            Object controller = loader.getController();
            return new Pair(controller, pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
