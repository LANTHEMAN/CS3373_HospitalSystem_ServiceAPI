package edu.wpi.cs3733d18.teamF.gfx.impl;

import edu.wpi.cs3733d18.teamF.MapSingleton;
import edu.wpi.cs3733d18.teamF.gfx.NodeDrawable;
import edu.wpi.cs3733d18.teamF.graph.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RestroomNodeDrawer extends NodeDrawable {

    private boolean isSelected = false;

    public RestroomNodeDrawer(Node node) {
        super(node);
    }

    public RestroomNodeDrawer() {
        super();
    }

    @Override
    public void selectNode() {
        isSelected = true;
    }

    @Override
    public void unselectNode() {
        isSelected = false;
    }

    @Override
    public void draw(Pane pane) {
        boolean is2D = MapSingleton.getInstance().getMap().is2D();
        double imageWidth = 5000;
        double imageHeight = is2D ? 3400 : 2772;
        double posX = is2D ? node.getPosition().getX() : node.getWireframePosition().getX();
        double posY = is2D ? node.getPosition().getY() : node.getWireframePosition().getY();

        if (!isSelected) {
            Circle circle = new Circle(1.5, Color.CYAN);
            circle.setCenterX(posX * pane.getMaxWidth() / imageWidth);
            circle.setCenterY(posY * pane.getMaxHeight() / imageHeight);
            pane.getChildren().add(circle);
        } else {
            Circle circle = new Circle(2, Color.LIGHTCYAN);
            circle.setCenterX(posX * pane.getMaxWidth() / imageWidth);
            circle.setCenterY(posY * pane.getMaxHeight() / imageHeight);
            pane.getChildren().add(circle);
        }
    }
}

