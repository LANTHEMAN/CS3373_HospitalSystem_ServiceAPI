package edu.wpi.cs3733d18.teamF.api;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServiceRequests sr = new ServiceRequests();
        try {
            sr.run(-1, -1, 1000, 631, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failure to start service requests");
        }
    }
}

