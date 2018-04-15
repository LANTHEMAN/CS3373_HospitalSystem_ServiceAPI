package edu.wpi.cs3733d18.teamF.controller.page;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public abstract class PageElement {
    protected void initElement(AnchorPane host, Pane root) {
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        host.getChildren().setAll(root);
    }
}
