package edu.wpi.cs3733d18.teamF.api;

import edu.wpi.cs3733d18.teamF.api.controller.UserSingleton;
import edu.wpi.cs3733d18.teamF.api.controller.PaneSwitcher;
import edu.wpi.cs3733d18.teamF.api.controller.Screens;
import edu.wpi.cs3733d18.teamF.api.sr.ServiceRequestSingleton;
import edu.wpi.cs3733d18.teamF.api.voice.VoiceLauncher;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Observable;

public class ServiceRequest {

    public ServiceRequest() {
    }

    static public void injectObservable(Observable o) {
        o.addObserver(VoiceLauncher.getInstance());
    }

    public void start(int xcoord, int ycoord, String cssPath) {
        Stage primaryStage = new Stage();

        Group root = new Group();


        Scene scene;
        int width, height;
        Application.setUserAgentStylesheet(null);
        if(ServiceRequestSingleton.getInstance().getPrefWidth() > 0 && ServiceRequestSingleton.getInstance().getPrefLength() > 0) {
            width = ServiceRequestSingleton.getInstance().getPrefWidth();
            height = ServiceRequestSingleton.getInstance().getPrefLength();
             scene = new Scene(root, width, height);
            primaryStage.setMaxWidth(width);
            primaryStage.setMaxHeight(height);
        }else{
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            width = gd.getDisplayMode().getWidth();
            height = gd.getDisplayMode().getHeight();
            scene = new Scene(root, width, height);
            primaryStage.setMaximized(true);
        }
        if(cssPath!= null) {
            scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());
        }
        else{
            scene.getStylesheets().add(Main.class.getResource("controller/default.css").toExternalForm());
        }
        PaneSwitcher paneSwitcher = new PaneSwitcher(scene, primaryStage);

        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("BWHIcon.png").toExternalForm());
        primaryStage.getIcons().add(image);

        // initial pane
        paneSwitcher.switchTo(Screens.Home);
        primaryStage.setTitle("Brigham and Women's Hospital");
        primaryStage.setScene(scene);
        if(xcoord >= 0) {
            primaryStage.setX(xcoord);
        }
        if(ycoord >= 0) {
            primaryStage.setY(ycoord);
        }
        primaryStage.show();
    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) {
        long fileSize = 0;

        // get rid of the database folder if its empty
        try {
            fileSize = Files.find(Paths.get("database_teamF_SR_API"), 3
                    , (p, bfa) -> bfa.isRegularFile())
                    .mapToLong(path -> path.toFile().length())
                    .sum();

            // delete the database if it is empty
            if (fileSize < 100) {
                Files.walk(Paths.get("database_teamF_SR_API"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
        }

        ServiceRequestSingleton.getInstance().setGridPaneDimensions(windowWidth, windowLength);
        if(destNodeID != null) {
            ServiceRequestSingleton.getInstance().setDestinationLocation(destNodeID);
        }

        initVoice();
        start(xcoord, ycoord, cssPath);
    }

    public void initVoice() {
        System.out.println("Initializing voice command");

        try {
            Thread t = new Thread(VoiceLauncher.getInstance());
            t.start();
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }
    }

    public void setCurrUser(String username){
        UserSingleton.getInstance().setCurrUser(username);
    }
    
    @Override
    public void finalize() {
        VoiceLauncher.getInstance().terminate();
    }

}
