package edu.wpi.cs3733d18.teamF.graph;

import edu.wpi.cs3733d18.teamF.Map;
import javafx.geometry.Point2D;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class NewNodeBuilder extends NodeBuilder<NewNodeBuilder> {
    // keeps track of the number of nodes of this node type
    private int numNodeType = -1;
    // elevator character
    private char linkChar;
    // the type of location this node is at
    private String nodeType = null;

    /**
     * A builder class to make entirely new nodes
     */
    public NewNodeBuilder() {
        super(NewNodeBuilder.class);
    }

    /**
     * @return the node built from this builder class
     */
    public Node build() {
        if (nodeType == null) {
            throw new AssertionError("A node type must be specified.");
        }
        if (nodeType.length() != 4) {
            throw new AssertionError("Node type must be 4 characters long.");
        }
        if (numNodeType == -1 && !(nodeType.equals("ELEV") || nodeType.equals("STAI"))) {
            throw new AssertionError("When creating a new node, you must set the number of " +
                    "nodes of that type.");
        }
        String numNodeTypeStr;
        if (nodeType.equals("ELEV") || nodeType.equals("STAI")) {
            if (linkChar == '\0') {
                throw new AssertionError("When creating an stair/elevator assign the Character.");
            }
            numNodeTypeStr = "00" + linkChar;
        } else {
            numNodeTypeStr = String.format("%03d", numNodeType);
        }

        if (floor == null) {
            throw new AssertionError("You must assign a floor to a node.");
        }

        String nodeID = "F" + nodeType + numNodeTypeStr + floor;

        if (position == null) {
            throw new AssertionError("You must set a position.");
        }
        if (wireframePosition == null) {
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

            wireframePosition = new Point2D(a * position.getX() + b * position.getY() + c,
                    d * position.getX() + e * position.getY() + f);
        }

        if (building == null) {
            throw new AssertionError("You must set a building.");
        }

        if (shortName == null) {
            shortName = nodeID + "[Unset shortName]";
        }
        if (longName == null) {
            longName = shortName + "[Unset longName]";
        }

        return new Node(position, wireframePosition, 0, nodeID, floor, building, nodeType, shortName, longName);
    }

    // only for testing node and graph!
    public NewNodeBuilder forceNumNodeType(int numNodeType){
        this.numNodeType = numNodeType;
        return this;
    }


    /**
     * @param map the map that this node will be added to
     * @return this to allow chained builder calls
     */
    public NewNodeBuilder setNumNodeType(Map map) {
        if (nodeType == null) {
            throw new AssertionError("Set node type before assigning a number");
        }

        if (nodeType.equals("ELEV") || nodeType.equals("STAI")) {
            throw new AssertionError("You must assign a linkChar to an elevator, not a numNodeType");
        }

        int typeCount = 0;
        try {
            typeCount = map.getNodes()
                    .stream()
                    .filter(node -> node.getNodeType().equals(nodeType))
                    .map(node -> node.getNodeID().substring(5, 8))
                    .map(Integer::parseInt)
                    .max(Integer::compare)
                    .get();
            typeCount++;
        } catch (Exception e) {
        }

        this.numNodeType = typeCount;
        return this;
    }

    /**
     * @param linkChar the elevator letter, only if this is an elevator
     * @return this to allow chained builder calls
     */
    public NewNodeBuilder setLinkChar(char linkChar) {
        if (!(Character.isLetter(linkChar))) {
            throw new AssertionError("You must assign a valid elevator character!");
        }

        this.linkChar = linkChar;
        return this;
    }

    /**
     * @param nodeType the type of this node
     * @return this to allow chained builder calls
     */
    public NewNodeBuilder setNodeType(String nodeType) {
        if (!(getNodeTypes().contains(nodeType))) {
            throw new AssertionError("The nodeType was invalid.");
        }
        this.nodeType = nodeType;
        return this;
    }

    /**
     * @param floor the floor of this node
     * @return this to allow chained builder calls
     */
    public NewNodeBuilder setFloor(String floor) {
        if (floor.equals("0") || floor.equals("0G")) {
            this.floor = "0G";
        } else if (floor.equals("1") || floor.equals("01")) {
            this.floor = "01";
        } else if (floor.equals("2") || floor.equals("02")) {
            this.floor = "02";
        } else if (floor.equals("3") || floor.equals("03")) {
            this.floor = "03";
        } else {
            if (!(floor.equals("L1") || floor.equals("L2"))) {
                throw new AssertionError("Unknown Floor Number. Must be any of: 0G, 01, 02, 03, L1, L2");
            }
            this.floor = floor;
        }
        return this;
    }
}
