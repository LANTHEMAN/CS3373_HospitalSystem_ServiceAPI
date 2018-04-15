package edu.wpi.cs3733d18.teamF.gfx.impl;

import edu.wpi.cs3733d18.teamF.MapSingleton;
import edu.wpi.cs3733d18.teamF.gfx.NodeDrawable;
import edu.wpi.cs3733d18.teamF.graph.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static java.lang.Math.sqrt;


public class StairNodeDrawer extends NodeDrawable {

    private boolean isSelected = false;
    public StairNodeDrawer() {
        super();
    }

    public StairNodeDrawer(Node node) {
        super(node);
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

        if(!isSelected){
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(new Double[]{
                    (posX * pane.getMaxWidth() / imageWidth),(posY * pane.getMaxHeight() / imageHeight) - (sqrt(3.0)/3) *3,
                    (posX * pane.getMaxWidth() / imageWidth)-(3.0/2),(posY * pane.getMaxHeight() / imageHeight)+(sqrt(3.0)/6)*3,
                    (posX * pane.getMaxWidth() / imageWidth)+(3.0/2),(posY * pane.getMaxHeight() / imageHeight)+(sqrt(3.0)/6)*3
            });
            triangle.setFill(Color.GREEN);
            pane.getChildren().add(triangle);
        }
        else{
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(new Double[]{
                    (posX * pane.getMaxWidth() / imageWidth),(posY * pane.getMaxHeight() / imageHeight) - (sqrt(3.0)/3) *4,
                    (posX * pane.getMaxWidth() / imageWidth)-(4.0/2),(posY * pane.getMaxHeight() / imageHeight)+(sqrt(3.0)/6)*4,
                    (posX * pane.getMaxWidth() / imageWidth)+(4.0/2),(posY * pane.getMaxHeight() / imageHeight)+(sqrt(3.0)/6)*4
            });
            triangle.setFill(Color.LIGHTGREEN);
            pane.getChildren().add(triangle);
        }
    }
}
