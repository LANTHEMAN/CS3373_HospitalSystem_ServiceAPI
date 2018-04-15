package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

import java.util.Observable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Node extends Observable {
    // the database ID of this node
    private final String nodeID;
    // the name of the floor where this node is located
    private final String floor;
    // the type of location this node is at
    private final String nodeType;
    // actual 3d position of node
    private Point3D position;
    // position of the node on the wireframe map
    private Point2D wireframePosition;
    // extra weight to cause A* to more likely avoid this node
    private double additionalWeight;
    // the name of the building this node is located in
    private String building;
    // an abbreviation of the name of this node
    private String shortName;
    // full name of this node
    private String longName;

    Node(Point3D position, Point2D wireframePosition, double additionalWeight, String nodeID, String floor, String building
            , String nodeType, String shortName, String longName) {
        this.position = position;
        this.wireframePosition = wireframePosition;
        this.additionalWeight = additionalWeight;
        this.nodeID = nodeID;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.shortName = shortName;
        this.longName = longName;
    }

    public static int getHeight(String floor) {
        int height;
        switch (floor) {
            case "01":
                height = 100;
                break;
            case "02":
                height = 200;
                break;
            case "03":
                height = 300;
                break;
            case "L2":
                height = -200;
                break;
            case "L1":
                height = -100;
                break;
            case "0G":
                height = 0;
                break;
            default:
                throw new AssertionError("Floor was not set");
        }
        return height;
    }

    /**
     * @return the id string of the node
     */
    public String getNodeID() {
        return nodeID;
    }

    private void setNodeID(String newNodeID) {
        // deletes this node and creates a new one
        signalClassChanged(newNodeID);
        // this node should be 'deleted' now
    }

    /**
     * Find the cartesian distance from this node to another node
     *
     * @param node The node to find the distance to
     * @return The distance from this node to node
     */
    public double displacementTo(Node node) {
        return (this.position.distance(node.position));
    }

    /**
     * Get the additional weight of this node
     *
     * @return The additional weight
     */
    public double getAdditionalWeight() {
        return additionalWeight;
    }

    /**
     * Set an additional weight for this node to make AStar avoid it
     *
     * @param additionalWeight The additional weight to add
     */
    public void setAdditionalWeight(double additionalWeight) {
        this.additionalWeight = additionalWeight;
    }

    /**
     * @return the floor of this node
     */
    public String getFloor() {
        return floor;
    }

    /**
     * @return the position of this node
     */
    public Point2D getPosition() {
        return new Point2D(position.getX(), position.getY());
    }

    /**
     * @param position the new position of this node
     */
    public void setPosition(Point2D position) {
        this.position = new Point3D(position.getX(), position.getY(), getHeight());

        double transX = 1203.7;
        double transY = 290.0;
        double transXp = 3.94;
        double transYp = 2.226;
        double scaleX = 0.736;
        double scaleY = 0.545;
        double rotateAngle = 0.17;

        double a = scaleX * cos(rotateAngle);
        double b = -scaleY * sin(rotateAngle) * transXp;
        double c = transX * scaleX * cos(rotateAngle) - transY * scaleY * sin(rotateAngle);
        double d = scaleX * sin(rotateAngle) * transYp;
        double e = scaleY * cos(rotateAngle);
        double f = transX * scaleX * sin(rotateAngle) + transY * scaleY * cos(rotateAngle);

        this.wireframePosition = new Point2D(a * position.getX() + b * position.getY() + c,
                d * position.getX() + e * position.getY() + f);

        signalClassChanged();
    }

    public int getHeight() {
        return getHeight(floor);
    }

    /**
     * @return the wireframe map position of this node
     */
    public Point2D getWireframePosition() {
        return wireframePosition;
    }

    /**
     * @param wireframePosition the new wireframe map position of this node
     */
    public void setWireframePosition(Point2D wireframePosition) {
        this.wireframePosition = wireframePosition;
        signalClassChanged();
    }

    /**
     * @return the building of this node
     */
    public String getBuilding() {
        return building;
    }

    /**
     * @param building the new building of this node
     */
    public void setBuilding(String building) {
        this.building = building;
        signalClassChanged();
    }

    /**
     * @return the node type of this node
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType the new node type of this node
     */
    public void setNodeType(String nodeType, int nodeTypeCount) {
        if (!(NodeBuilder.getNodeTypes().contains(nodeType))
                || nodeType.equals("ELEV") || nodeType.equals("STAI")) {
            throw new AssertionError("The nodeType was invalid.");
        }
        if (nodeTypeCount > 999 || nodeTypeCount < 0) {
            throw new AssertionError("The nodeTypeCount was out of bounds.");
        }

        // notify the change in nodeID
        String newNodeID = nodeID.substring(0, 1)
                + nodeType
                + String.format("%03d", nodeTypeCount)
                + nodeID.substring(8);
        setNodeID(newNodeID);
    }

    public void setNodeType(String nodeType, char linkChar) {
        if (!(nodeType.equals("ELEV") || nodeType.equals("STAI"))) {
            throw new AssertionError("The nodeType was invalid.");
        }
        if (!(Character.isLetter(linkChar))) {
            throw new AssertionError("You must assign a valid link character!");
        }

        // notify the change in nodeID
        String newNodeID = nodeID.substring(0, 1)
                + nodeType
                + "00" + linkChar
                + nodeID.substring(8);
        setNodeID(newNodeID);
    }

    /**
     * @return the short name of this node
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the new short name of this node
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
        signalClassChanged();
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
        signalClassChanged();
    }

    private void signalClassChanged() {
        this.setChanged();
        this.notifyObservers();
    }

    private void signalClassChanged(Object arg) {
        this.setChanged();
        this.notifyObservers(arg);
    }

}
