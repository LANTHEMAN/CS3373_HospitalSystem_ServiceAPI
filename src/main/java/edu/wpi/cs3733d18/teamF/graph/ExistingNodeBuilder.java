package edu.wpi.cs3733d18.teamF.graph;

public class ExistingNodeBuilder extends NodeBuilder<ExistingNodeBuilder> {
    // the nodeType of this Node
    String nodeType = null;
    // the database ID of this node
    private String nodeID = null;

    /**
     * A builder class to make nodes from existing entries in the database
     */
    public ExistingNodeBuilder() {
        super(ExistingNodeBuilder.class);
    }

    /**
     * @return the node built from this builder class
     */
    public Node build() {
        // there must be a nodeID
        if (nodeID == null) {
            throw new AssertionError("Existing nodes must have a nodeID.");
        }
        if (position == null) {
            throw new AssertionError("Node must contain a position");
        }
        if (wireframePosition == null) {
            throw new AssertionError("Node must contain a wireframePosition");
        }
        if (shortName == null) {
            throw new AssertionError("Node must contain a shortName");
        }
        if(longName == null){
            throw new AssertionError("Node must contain a longName");
        }
        if (building == null) {
            throw new AssertionError("Node must contain a building");
        }

        return new Node(position, wireframePosition, 0, nodeID, floor, building, nodeType, shortName, longName);
    }

    public ExistingNodeBuilder setNodeID(String nodeID) {
        // parse fields from the nodeID if it exists
        if (nodeID.length() != 10) {
            throw new AssertionError("Invalid Node ID format. Not 10 characters 1ong.");
        }
        nodeType = nodeID.substring(1, 5);
        // inconsistent csv files
        if(nodeType.equals("BATH")){
            nodeType = "REST";
        }

        if (!(getNodeTypes().contains(nodeType))) {
            throw new AssertionError("The nodeType of the nodeID was invalid: " + nodeType);
        }
        floor = nodeID.substring(nodeID.length() - 2);
        if (!(getFloors().contains(floor))) {
            throw new AssertionError("Unknown Floor Number. Must be any of: 0G, 01, 02, 03, L1, L2");
        }
        // set the nodeID last to ensure that it was valid
        this.nodeID = nodeID;
        return this;
    }
}

