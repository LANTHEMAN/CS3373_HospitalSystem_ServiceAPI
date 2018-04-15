package edu.wpi.cs3733d18.teamF.gfx.impl;

import edu.wpi.cs3733d18.teamF.MapSingleton;
import edu.wpi.cs3733d18.teamF.gfx.EdgeDrawable;
import edu.wpi.cs3733d18.teamF.graph.Edge;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class LineEdgeDrawer extends EdgeDrawable {

    private Paint color = Color.BLACK;

    public LineEdgeDrawer(Edge edge) {
        super(edge);
    }

    public LineEdgeDrawer() {
        super();
    }

    public LineEdgeDrawer(Paint color) {
        super();
        this.color = color;
    }

    @Override
    public void draw(Pane pane) {
        boolean is2D = MapSingleton.getInstance().getMap().is2D();
        double imageWidth = 5000;
        double imageHeight = is2D ? 3400 : 2772;
        double posX1 = is2D ? edge.getNode1().getPosition().getX() : edge.getNode1().getWireframePosition().getX();
        double posY1 = is2D ? edge.getNode1().getPosition().getY() : edge.getNode1().getWireframePosition().getY();
        double posX2 = is2D ? edge.getNode2().getPosition().getX() : edge.getNode2().getWireframePosition().getX();
        double posY2 = is2D ? edge.getNode2().getPosition().getY() : edge.getNode2().getWireframePosition().getY();

        Line line = new Line();
        line.setStartX(posX1 * pane.getMaxWidth() / imageWidth);
        line.setStartY(posY1 * pane.getMaxHeight() / imageHeight);
        line.setEndX(posX2 * pane.getMaxWidth() / imageWidth);
        line.setEndY(posY2 * pane.getMaxHeight() / imageHeight);
        line.setStroke(color);
        pane.getChildren().add(line);
    }
}
