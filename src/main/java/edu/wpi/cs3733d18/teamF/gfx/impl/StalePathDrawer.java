package edu.wpi.cs3733d18.teamF.gfx.impl;

import edu.wpi.cs3733d18.teamF.gfx.EdgeDrawable;
import edu.wpi.cs3733d18.teamF.gfx.PathDrawable;
import edu.wpi.cs3733d18.teamF.graph.Edge;
import javafx.scene.layout.Pane;

public class StalePathDrawer extends PathDrawable {

    private EdgeDrawable edgeDrawer;

    public StalePathDrawer(EdgeDrawable edgeDrawer) {
        this.edgeDrawer = edgeDrawer;
    }

    @Override
    public void draw(Pane pane) {
        for (Edge edge : path.getEdges()) {
            edgeDrawer.update(edge);
            edgeDrawer.draw(pane);
        }
    }
}
