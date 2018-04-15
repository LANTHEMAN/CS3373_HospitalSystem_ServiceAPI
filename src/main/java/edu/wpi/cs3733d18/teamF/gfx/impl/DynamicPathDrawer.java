package edu.wpi.cs3733d18.teamF.gfx.impl;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733d18.teamF.MapSingleton;
import edu.wpi.cs3733d18.teamF.gfx.PathDrawable;
import edu.wpi.cs3733d18.teamF.graph.Edge;
import edu.wpi.cs3733d18.teamF.graph.Node;
import edu.wpi.cs3733d18.teamF.graph.Path;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;

import static java.lang.Math.ceil;

/**
 * A animated path with arrows moving from the source to the destination
 */
public class DynamicPathDrawer extends PathDrawable {
    // used to play the animation
    private Timeline timeline = null;
    // stores every sprite and sprite progress along path
    private ArrayList<Arrow> arrows = new ArrayList<>();
    // how often, in milliseconds, between timeline updates
    private int timestep = 300;

    /**
     * This ctor takes no parameters, it must be updated with a new Path using the {@link #update(Path)} function.
     */
    public DynamicPathDrawer() {
        super();
    }

    /**
     * Adds all the arrows to the pane and starts the animation
     *
     * @param pane where to draw the arrows to
     */
    @Override
    public void draw(Pane pane) {
        if (timeline != null) {
            timeline.stop();
        }
        if (path.getEdges().size() < 1) {
            return;
        }

        initAndDrawArrows(pane);

        // update each arrow's animation every timestep milliseconds
        timeline = new Timeline(new KeyFrame(Duration.millis(timestep), event -> {
            for (Arrow arrow : arrows) {
                // set the speed of the arrows
                arrow.progress += timestep / 10;

                Pair<Pair<Node, Node>, Double> pathPos = getPathPos(arrow.progress);

                // if the arrow went off of the end of the path, bring it back to the beginning
                if (pathPos == null) {
                    arrow.progress -= path.getUnweightedLength();
                    arrow.view.setVisible(false);
                    arrow.prevX = null;
                    arrow.prevY = null;
                    continue;
                }

                Node src = pathPos.getKey().getKey();
                Node dst = pathPos.getKey().getValue();
                double distBetweenNodes = pathPos.getValue();

                String mapFloor = MapSingleton.getInstance().getMap().getFloor();
                if (!mapFloor.equals(src.getFloor()) || !mapFloor.equals(dst.getFloor())) {
                    arrow.view.setVisible(false);
                    arrow.prevX = null;
                    arrow.prevY = null;
                    continue;
                }

                // party colors!
                arrow.view.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                arrow.view.setVisible(true);

                double angle = getAngleTo(src, dst);
                Point2D newPosition = getNewScreenPosition(src, dst, distBetweenNodes, pane);

                // save the previous position and angle of the arrow for interpolation
                if (arrow.prevX == null && arrow.prevY == null) {
                    arrow.prevX = newPosition.getX();
                    arrow.prevY = newPosition.getY();
                    arrow.view.setTranslateX(arrow.prevX);
                    arrow.view.setTranslateY(arrow.prevY);
                    arrow.view.setRotate(angle);
                } else {
                    arrow.prevX = arrow.view.getTranslateX();
                    arrow.prevY = arrow.view.getTranslateY();
                }
                arrow.prevAngle = arrow.view.getRotate();

                interpolateArrow(arrow, newPosition, angle);
            }
        }));

        // start playing the animation
        // the timeline will exist until the function {@link #update(Path)} is called to clear the drawable path
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Returns the destination of the arrow
     *
     * @param src              the source node of the arrow
     * @param dst              the destination of the arrow
     * @param distBetweenNodes how far the arrow is between the nodes
     * @param pane             where the nodes will be drawn to
     * @return where to draw the arrow
     */
    private Point2D getNewScreenPosition(Node src, Node dst, double distBetweenNodes, Pane pane) {
        boolean is2D = MapSingleton.getInstance().getMap().is2D();
        double imageWidth = 5000;
        double imageHeight = is2D ? 3400 : 2772;
        double posX1 = is2D ? src.getPosition().getX() : src.getWireframePosition().getX();
        double posY1 = is2D ? src.getPosition().getY() : src.getWireframePosition().getY();
        double posX2 = is2D ? dst.getPosition().getX() : dst.getWireframePosition().getX();
        double posY2 = is2D ? dst.getPosition().getY() : dst.getWireframePosition().getY();

        double screenPosX = (posX1 + ((posX2 - posX1) * (distBetweenNodes / src.displacementTo(dst))) - 35)
                * pane.getMaxWidth() / imageWidth;
        double screenPosY = (posY1 + ((posY2 - posY1) * (distBetweenNodes / src.displacementTo(dst))) + 28)
                * pane.getMaxHeight() / imageHeight;
        return new Point2D(screenPosX, screenPosY);
    }


    /**
     * Finds the angle to the destination node from the start node
     *
     * @param src the starting point
     * @param dst the ending point
     * @return the angle
     */
    private double getAngleTo(Node src, Node dst) {
        boolean is2D = MapSingleton.getInstance().getMap().is2D();
        double posX1 = is2D ? src.getPosition().getX() : src.getWireframePosition().getX();
        double posY1 = is2D ? src.getPosition().getY() : src.getWireframePosition().getY();
        double posX2 = is2D ? dst.getPosition().getX() : dst.getWireframePosition().getX();
        double posY2 = is2D ? dst.getPosition().getY() : dst.getWireframePosition().getY();
        // calculate the next angle of the arrow
        return Math.toDegrees(Math.atan2(posY2 - posY1, posX2 - posX1)) + 90;
    }


    /**
     * @param progress The progress on an arrow down the path
     * @return The 2 nodes the arrow is between, src then dst, and the progress from src to dst
     */
    private Pair<Pair<Node, Node>, Double> getPathPos(double progress) {
        Node src;
        Node dst = path.getNodes().get(0);
        for (Edge edge : path.getEdges()) {
            src = dst;
            dst = (edge.getNode1() == src) ? edge.getNode2() : edge.getNode1();
            if (progress < edge.getDistance()) {
                return new Pair<>(new Pair<>(src, dst), progress);
            }
            progress -= edge.getDistance();
        }
        // if drawing off of the path
        return null;
    }

    /**
     * Animates the arrow to a new position
     *
     * @param arrow    the arrow to interpolate
     * @param newPos   the new position to interpolate to
     * @param newAngle the new angle to interpolate to
     */
    private void interpolateArrow(Arrow arrow, Point2D newPos, double newAngle) {
        // interpolate the arrow to the next position
        TranslateTransition translateTransition =
                new TranslateTransition(Duration.millis(timestep + 10), arrow.view);
        translateTransition.setFromX(arrow.prevX);
        translateTransition.setToX(newPos.getX());
        translateTransition.setFromY(arrow.prevY);
        translateTransition.setToY(newPos.getY());
        translateTransition.setCycleCount(1);
        translateTransition.play();

        // fix the arrow spinning around if the delta angle is greater than 18 degrees
        if (arrow.prevAngle - newAngle > 180) {
            newAngle += 360;
        } else if (arrow.prevAngle - newAngle < -180) {
            newAngle -= 360;
        }
        // interpolate the arrow to the next arrow
        RotateTransition rotateTransition =
                new RotateTransition(Duration.millis(timestep), arrow.view);
        rotateTransition.setFromAngle(arrow.prevAngle);
        rotateTransition.setToAngle(newAngle);
        rotateTransition.play();
    }

    /**
     * creates all of the arrows and adds them to the pane
     *
     * @param pane the pane that contains the arrows
     */
    private void initAndDrawArrows(Pane pane) {
        arrows.clear();

        // determine the distance between arrows
        double divDist = 40;
        double len = path.getUnweightedLength();
        int divs = (int) ceil(len / divDist);
        divDist = len / divs;

        // create arrows distributed along the path
        for (int i = 0; i < divs; i++) {
            arrows.add(new Arrow(i * divDist));
        }

        // add the arrows to the pane
        for (Arrow arrow : arrows) {
            arrow.view.setVisible(false);
            pane.getChildren().add(arrow.view);
            arrow.prevX = null;
            arrow.prevY = null;
        }
    }


    /**
     * Change the path of the animation
     *
     * @param path the new path, pass in null to clear the timeline
     */
    @Override
    public void update(Path path) {
        arrows.clear();
        this.path = path;
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * A temporary class to hold the state, position, and reference to the sprite for each arrow
     */
    private static class Arrow {
        // the drawn sprite
        FontAwesomeIconView view = new FontAwesomeIconView(FontAwesomeIcon.CHEVRON_UP);
        // the position down the path
        double progress;
        // the previous x position to interpolate from
        Double prevX = null;
        // the previous y position  to interpolate from
        Double prevY = null;
        // the previous angle to interpolate from
        double prevAngle = 0;

        /**
         * @param progress the initial progress of the arrow
         */
        Arrow(double progress) {
            this.progress = progress;
            view.setFill(Color.BLACK);
            view.setScaleX(0.3);
            view.setScaleY(0.3);
        }
    }
}
