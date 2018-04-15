package edu.wpi.cs3733d18.teamF.graph;

import java.util.ArrayList;

public class Path {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    /**
     * A collection of nodes and edges representing a path along a graph
     *
     * @param path  the list of nodes
     * @param graph the graph containing the nodes
     */
    public Path(ArrayList<Node> path, Graph graph) {
        this.nodes = path;
        this.edges = new ArrayList<>();

        // get edges
        Node prevNode = null;
        for (Node node : nodes) {
            if (prevNode != null) {
                edges.add(graph.getEdge(node, prevNode));
            }
            prevNode = node;
        }

        //if (!(nodes.size() > 1 && nodes.size() != edges.size() - 1)) {
        //    throw new AssertionError("The nodes must be connected by edges!");
        //}
    }

    public double getLength() {
        return edges.stream()
                .map(node -> node.getDistance() + node.getNode1().getAdditionalWeight() + node.getNode2().getAdditionalWeight())
                .mapToDouble(value -> value)
                .sum();
    }


    /**
     * @return the nodes of this path
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * @return the edges in this path
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Path)) {
            return false;
        }
        Path path = (Path) obj;
        return this.nodes.equals(path.nodes);
    }

    public ArrayList<String> makeTextDirections() {
        ArrayList<String> directions = new ArrayList<>();

        double dist = 0;

        for (int nodeIndex = 1; nodeIndex < this.nodes.size() - 1; nodeIndex++) {

            Node previousNode = this.getNodes().get(nodeIndex - 1);
            Node currentNode = this.getNodes().get(nodeIndex);
            Node nextNode = this.getNodes().get(nodeIndex + 1);

            double angle = getAngle(previousNode, currentNode, nextNode);

            dist += previousNode.displacementTo(currentNode);

            if(currentNode.getNodeType().equals("ELEV") && nextNode.getNodeType().equals("ELEV")){
                directions.add("Take Elevator to floor: " + nextNode.getFloor());
                continue;
            }else if(currentNode.getNodeType().equals("STAI") && nextNode.getNodeType().equals("ELEV")){
                directions.add("Take Stairs to floor: " + nextNode.getFloor());
                continue;
            }

            if (angle < -30) {
                directions.add(String.format("Walk straight for %.0f feet", dist));
                if(currentNode.getNodeType().equals("HALL"))
                    directions.add("Turn Left");
                else
                    directions.add("Turn Left at " + currentNode.getShortName());
                dist = 0;
            } else if (angle > 30) {
                directions.add(String.format("Walk straight for %.0f feet", dist));
                if(currentNode.getNodeType().equals("HALL"))
                    directions.add("Turn Right");
                else
                    directions.add("Turn Right at " + currentNode.getShortName());
                dist = 0;
            }
        }

        directions.add(String.format("Walk straight for %.0f feet", nodes.get(nodes.size()-2).displacementTo(nodes.get(nodes.size()-1)) + dist));
        directions.add("Arrive at " + nodes.get(nodes.size()-1).getShortName());

        return directions;
    }

    public double getAngle(Node previousNode, Node currentNode, Node nextNode) {

        double v1X = currentNode.getPosition().getX() - previousNode.getPosition().getX();
        double v1Y = currentNode.getPosition().getY() - previousNode.getPosition().getY();

        double v2X = nextNode.getPosition().getX() - currentNode.getPosition().getX();
        double v2Y = nextNode.getPosition().getY() - currentNode.getPosition().getY();

        double dot = v1X * v2X + v1Y * v2Y;
        double det = v1X * v2Y - v1Y * v2X;

        return Math.toDegrees(Math.atan2(det, dot));
    }

    public double getUnweightedLength(){
        return edges.stream()
                .map(Edge::getDistance)
                .mapToDouble(value -> value)
                .sum();
    }
}
