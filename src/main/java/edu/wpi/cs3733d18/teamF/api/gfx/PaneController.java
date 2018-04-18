package edu.wpi.cs3733d18.teamF.api.gfx;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public abstract class PaneController {
    private Pane root;
    private boolean isVisible = true;
    private ArrayList<Drawable> drawables = new ArrayList<>();


    protected PaneController(Pane root) {
        this.root = root;
    }

    public final void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;
        clear();
        if (isVisible) {
            draw();
        }
    }

    public final boolean getVisiblilty() {
        return isVisible;
    }

    protected final void addDrawable(Drawable drawable) {
        drawables.add(drawable);
    }

    protected final void removeDrawable(Drawable drawable) {
        drawables.remove(drawable);
    }

    protected final void clearDrawables() {
        drawables.clear();
    }

    protected final void draw(){
        for (Drawable drawable : drawables) {
            drawable.draw(root);
        }
        setUnclickable(root);
    }

    private void setUnclickable(Pane parent) {
        for (Node component : parent.getChildren()) {
            if (component instanceof Pane) {
                //if the component is a container, scan its children
                setUnclickable((Pane) component);
            } else {
                component.setMouseTransparent(true);
                component.setPickOnBounds(false);
            }
        }
    }

    protected final void clear(){
        root.getChildren().clear();
    }

}
