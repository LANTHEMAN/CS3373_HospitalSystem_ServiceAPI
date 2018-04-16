package edu.wpi.cs3733d18.teamF.api.controller;

import edu.wpi.cs3733d18.teamF.api.controller.page.MainPage;
import edu.wpi.cs3733d18.teamF.api.controller.page.*;

public class Screens {

    public static final Screen Home = new Screen("mainPage.fxml", MainPage.class);


    static class Screen {
        public final String fxmlFile;
        public final Class<? extends SwitchableController> Controller;

        Screen(String fxmlFile, Class<? extends SwitchableController> Controller) {
            this.fxmlFile = fxmlFile;
            this.Controller = Controller;
        }

    }

}
