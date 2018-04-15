package edu.wpi.cs3733d18.teamF.gfx;

import edu.wpi.cs3733d18.teamF.graph.Node;

public abstract class NodeDrawable implements Drawable{
    protected Node node;

    protected NodeDrawable(Node node) {
        this.node = node;
    }

    protected NodeDrawable() {

    }

    public void update(Node node){
        this.node = node;
    }

    public abstract void selectNode();
    public abstract void unselectNode();
}
