package edu.wpi.cs3733d18.teamF;

import edu.wpi.cs3733d18.teamF.controller.PaneSwitcher;
import edu.wpi.cs3733d18.teamF.controller.Screens;
import edu.wpi.cs3733d18.teamF.voice.VoiceLauncher;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Main extends Application {
    public static void main(String[] args) throws IOException, InterruptedException, JAXBException {
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
        } catch (IOException e) {
        }

        System.out.println("Initializing voice command");

        Thread t = new Thread(VoiceLauncher.getInstance());
        t.start();

        launch(args);

        VoiceLauncher.getInstance().terminate();
        t.join();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Group root = new Group();

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        Scene scene = new Scene(root, width, height);
        PaneSwitcher paneSwitcher = new PaneSwitcher(scene);

        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("BWHIcon.png").toExternalForm());
        primaryStage.getIcons().add(image);

        // initial pane
        paneSwitcher.switchTo(Screens.Home);

        primaryStage.setTitle("Brigham and Women's Hospital");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}

