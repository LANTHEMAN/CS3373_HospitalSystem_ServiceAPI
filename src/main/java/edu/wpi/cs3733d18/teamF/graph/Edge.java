package edu.wpi.cs3733d18.teamF.graph;

public class Edge {
    private String edgeID;
    private Node node1;
    private Node node2;

    /**
     * Create a new edge
     *
     * @param node1  The first node that this edge is connected to
     * @param node2  The second node that this edge is connected to
     * @param edgeID The String ID of this node
     */
    public Edge(Node node1, Node node2, String edgeID) {
        this.edgeID = edgeID;
        this.node1 = node1;
        this.node2 = node2;
    }

    /**
     * @return the edgeID of this edge
     */
    public String getEdgeID() {
        return edgeID;
    }

    public boolean hasNode(Node node) {
        return (node == node1 || node == node2);
    }

    /**
     * Determine if this edge is the edge between two nodes
     *
     * @param node1 The first node
     * @param node2 The second node
     * @return True if this edge is between these two nodes, false otherwise
     */
    public boolean edgeOfNodes(Node node1, Node node2) {
        return ((this.node1 == node1 && this.node2 == node2) ||
                this.node2 == node1 && this.node1 == node2);
    }

    /**
     * @return one of the nodes
     */
    public Node getNode1() {
        return node1;
    }

    /**
     * @return one of the nodes
     */
    public Node getNode2() {
        return node2;
    }

    public double getDistance(){
        return node1.displacementTo(node2);
    }
}
