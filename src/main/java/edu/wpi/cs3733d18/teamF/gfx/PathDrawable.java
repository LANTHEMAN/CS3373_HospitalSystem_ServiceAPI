package edu.wpi.cs3733d18.teamF.gfx;

import edu.wpi.cs3733d18.teamF.graph.Path;

public abstract class PathDrawable implements Drawable {
    protected Path path;

    protected PathDrawable(Path path) {
        this.path = path;
    }

    protected PathDrawable() {
    }

    public void update(Path path) {
        this.path = path;
    }
}
