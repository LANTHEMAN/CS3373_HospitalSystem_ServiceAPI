package edu.wpi.cs3733d18.teamF;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServiceRequest sr = new ServiceRequest();
        try{
            sr.run(0,0,1900,1000, null, null, null);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Failure to start service requests");
        }
    }
}

