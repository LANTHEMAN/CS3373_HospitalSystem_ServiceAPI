package edu.wpi.cs3733d18.teamF.graph;

import java.util.*;

public class AStar implements PathFindingAlgorithm{

    /**
     * Static function to determine the best route from one node to another
     * given a graph
     *
     * @param graph       The graph to find path on
     * @param source      The source node in the graph
     * @param destination The destination node in the graph
     * @return An array of Nodes that represent a path through the graph
     */

    public  Path getPath(Graph graph, Node source, Node destination) {
        // see if the destination exists or if src equals dst
        if (destination == null || source == null) {
            throw new AssertionError();
        } else if (source == destination) {
            ArrayList<Node> path = new ArrayList<>();
            path.add(source);
            return new Path(path, graph);
        }

        // ensures that every Node only has 1 corresponding AStarNode
        HashMap<Node, AStarNode> knownNodes = new HashMap<>();
        knownNodes.put(source, new AStarNode(source, destination, 0, knownNodes));
        knownNodes.put(destination, new AStarNode(destination, destination, Double.MAX_VALUE, knownNodes));

        // nodes that have already been evaluated
        HashSet<AStarNode> closedSet = new HashSet<>();
        // nodes that are discovered but not evaluated
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();

        // for each node, it saves where it can most efficiently be reached from
        HashMap<AStarNode, AStarNode> cameFrom = new HashMap<>();

        // set up initial conditions with source node
        AStarNode sourceNode = knownNodes.get(source);
        AStarNode dstNode = knownNodes.get(destination);
        openSet.add(sourceNode);

        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            if (currentNode == dstNode) {
                ArrayList<Node> path = new ArrayList<>();
                path.add(currentNode.getNode());
                AStarNode itNode = currentNode;
                while (cameFrom.containsKey(itNode)) {
                    itNode = cameFrom.get(itNode);
                    path.add(itNode.getNode());
                }
                Collections.reverse(path);
                return new Path(path, graph);
            }

            closedSet.add(currentNode);

            LinkedList<AStarNode> neighbors = currentNode.getNeighbors(graph);
            for (AStarNode neighbor : neighbors) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }

                double costCurrentToNeighbor = currentNode.getGScore()
                        + currentNode.displacementTo(neighbor)
                        + neighbor.node.getAdditionalWeight();

                // this is a worse path to the same point
                if (costCurrentToNeighbor > neighbor.getGScore()) {
                    continue;
                }

                // this path is the best to this point
                cameFrom.put(neighbor, currentNode);

                // update nodes and priority queue
                neighbor.setDistanceTraveled(costCurrentToNeighbor);
                openSet.remove(neighbor);
                openSet.add(neighbor);
            }
        }

        // no path was found
        return new Path(new ArrayList<>(), graph);
    }

    // wrapper class for Node that allows priority queue comparisons
    private static class AStarNode implements Comparable<AStarNode> {
        private Node node;
        private Node destination;
        private double distanceTraveled;
        private HashMap<Node, AStarNode> knownNodes;

        /**
         * A specific version of a node that stores values specific to this path
         *
         * @param node             The current node to base the AStarNode off of
         * @param destination      The destination node in the graph
         * @param distanceTraveled The distance traveled up until this node
         * @param knownNodes       The known nodes along this path so far
         */
        private AStarNode(Node node, Node destination, double distanceTraveled
                , HashMap<Node, AStarNode> knownNodes) {
            this.node = node;
            this.destination = destination;
            this.distanceTraveled = distanceTraveled;
            this.knownNodes = knownNodes;
        }

        /**
         * Find cartesian distance between two nodes
         *
         * @param node The node to find the distance to
         * @return The distance from this node to the destination node
         */
        private double displacementTo(AStarNode node) {
            return this.node.displacementTo(node.node);
        }

        /**
         * Get all the neighbors of this node
         *
         * @param graph The graph that contains this node
         * @return A LinkedList of all the neighbor nodes
         */
        private LinkedList<AStarNode> getNeighbors(Graph graph) {
            LinkedList<AStarNode> neighbors = new LinkedList<>();
            for (Node neighbor : graph.getNeighbors(this.node)) {
                if (knownNodes.containsKey(neighbor)) {
                    neighbors.add(knownNodes.get(neighbor));
                } else {
                    AStarNode newNode = new AStarNode(neighbor, destination, Double.MAX_VALUE, knownNodes);
                    knownNodes.put(neighbor, newNode);
                    neighbors.add(newNode);
                }
            }
            return neighbors;
        }

        /**
         * Get this node
         *
         * @return This node
         */
        private Node getNode() {
            return node;
        }

        /**
         * Set the distance the distance traveled for this node so far
         *
         * @param distanceTraveled The new distance traveled
         */
        private void setDistanceTraveled(double distanceTraveled) {
            this.distanceTraveled = distanceTraveled;
        }

        /**
         * Get the G score for AStar algorithm
         *
         * @return The G score
         */
        private double getGScore() {
            return distanceTraveled + node.getAdditionalWeight();
        }

        /**
         * Get the F score for AStar algorithm
         *
         * @return The F Score
         */
        private double getFScore() {
            return distanceTraveled + this.node.displacementTo(this.destination) + node.getAdditionalWeight();
        }

        /**
         * Compare two nodes for priority queue purposes
         *
         * @param other The other node to compare
         * @return Negative if this node is "less", positive otherwise
         */
        @Override
        public int compareTo(AStarNode other) {
            return getFScore() < other.getFScore() ? -1 : 1;
        }
    }

}
