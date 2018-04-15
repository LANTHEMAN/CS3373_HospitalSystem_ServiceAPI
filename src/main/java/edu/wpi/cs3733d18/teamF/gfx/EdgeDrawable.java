package edu.wpi.cs3733d18.teamF.gfx;

import edu.wpi.cs3733d18.teamF.graph.Edge;

public abstract class EdgeDrawable implements Drawable{
    protected Edge edge = null;

    protected EdgeDrawable(Edge edge){
        this.edge = edge;
    }

    protected EdgeDrawable() {
    }

    public void update(Edge edge){
        this.edge = edge;
    }
}