package edu.wpi.cs3733d18.teamF.api;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.wpi.cs3733d18.teamF.api.controller.Screens;
import edu.wpi.cs3733d18.teamF.api.controller.PaneSwitcher;
import edu.wpi.cs3733d18.teamF.api.sr.ServiceRequestSingleton;
import edu.wpi.cs3733d18.teamF.api.voice.VoiceLauncher;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ServiceRequest {
    public ServiceRequest(){}

    public void start() {
        Stage primaryStage = new Stage();

        Group root = new Group();

        int width = ServiceRequestSingleton.getInstance().getPrefWidth();
        int height = ServiceRequestSingleton.getInstance().getPrefLength();

        Scene scene = new Scene(root, width, height);
        PaneSwitcher paneSwitcher = new PaneSwitcher(scene);

        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("BWHIcon.png").toExternalForm());
        primaryStage.getIcons().add(image);

        // initial pane
        paneSwitcher.switchTo(Screens.Home);

        primaryStage.setTitle("Brigham and Women's Hospital");
        primaryStage.setMaxWidth(width);
        primaryStage.setMaxHeight(height);
        primaryStage.setScene(scene);
        //primaryStage.setMaximized(true);
        //primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) {
        long fileSize = 0;
        // get rid of the database folder if its empty
        try {
            fileSize = Files.find(Paths.get("database"), 3
                    , (p, bfa) -> bfa.isRegularFile())
                    .mapToLong(path -> path.toFile().length())
                    .sum();

            // delete the database if it is empty
            if (fileSize < 100) {
                Files.walk(Paths.get("database"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {}

        ServiceRequestSingleton.getInstance().setGridPaneDimensions(windowWidth, windowLength);

        initVoice();
        start();
    }

    public void initVoice() {
        System.out.println("Initializing voice command");

        Thread t = new Thread(VoiceLauncher.getInstance());

        try {
            t.start();
        }catch(Exception e){}
    }

    public void setCurrUser(String username){
        ServiceRequestSingleton.getInstance().setCurrUser(username);
    }
}
