package edu.wpi.cs3733d18.teamF.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Graph {
    private HashMap<Node, HashSet<Node>> adjacencyList;
    private HashSet<Edge> edges;

    /**
     * Create a new graph of the hospital
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.edges = new HashSet<>();
    }

    /**
     * Add a node to this graph
     *
     * @param node The node to add
     * @return The graph with the new node
     */
    public Graph addNode(Node node) {
        adjacencyList.put(node, new HashSet<>());
        return this;
    }

    /**
     * Remove a node from the graph it it exists
     *
     * @param node The node to remove
     * @return The graph if successful, null if not
     */
    public Graph removeNode(Node node) {
        if (!adjacencyList.containsKey(node)) {
            return null;
        }

        // remove all edges containing node from adjacency list
        // must be a copy because of concurrent modification and iteration
        // would only hold 1-4 node connections anyways
        HashSet<Node> adjacentNodes = new HashSet<>(adjacencyList.get(node));
        for (Node adjacentNode : adjacentNodes) {
            // remove edge from edges set
            removeEdge(node, adjacentNode);
        }
        // remove the node
        adjacencyList.remove(node);

        return this;
    }

    /**
     * used to create an entirely new edge
     *
     * @param node1 the first node
     * @param node2 the second node
     * @return a reference to this to allow chained calls during testing
     */
    public Graph addEdge(Node node1, Node node2) {
        // make sure both nodes exist
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)) {
            throw new AssertionError("Attempted to add an edge from non-existent node(s)");
        }
        // if the node already exists
        if (edges.stream()
                .anyMatch(edge -> (edge.getNode1() == node1 && edge.getNode2() == node2)
                        || (edge.getNode1() == node2 && edge.getNode2() == node1))) {
            return this;
        }
        return addEdge(node1, node2, node1.getNodeID() + "_" + node2.getNodeID());
    }

    /**
     * Add an edge between two nodes with an existing nodeID
     *
     * @param node1  The first node
     * @param node2  The second node
     * @param edgeID The id of this edge
     * @return a reference to this to allow chained calls during testing
     */
    public Graph addEdge(Node node1, Node node2, String edgeID) {
        // make sure both nodes exist
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)) {
            throw new AssertionError("Attempted to add an edge from non-existent node(s)");
        }

        // double ch if the edge exists in the adjacency list
        HashSet<Node> adjacentNodes = adjacencyList.get(node1);

        // check if the edge already exists
        if (edges.stream().anyMatch(edge -> edge.getEdgeID().equals(edgeID))) {
            if (edges.stream().anyMatch(edge -> (edge.getNode1() == node1 && edge.getNode2() == node2)
                    || (edge.getNode1() == node2 && edge.getNode2() == node1))) {
                if (!adjacentNodes.contains(node2)) {
                    throw new AssertionError("An Edge exists, but the Nodes do not actually connect! RIP");
                }
                return this;
            } else {
                throw new AssertionError("Edge ID already exists!");
            }
        }

        // add the edge if it does not exist
        adjacentNodes.add(node2);
        adjacencyList.get(node2).add(node1);

        edges.add(new Edge(node1, node2, edgeID));

        return this;
    }

    /**
     * Remove an edge from this graph
     *
     * @param node1 The first node that this edge is connected to
     * @param node2 The second node that this edge is connected to
     * @return a reference to this to allow chained calls during testing
     */
    public Graph removeEdge(Node node1, Node node2) {
        // make sure both nodes exist
        if (!adjacencyList.containsKey(node1) || !adjacencyList.containsKey(node2)) {
            throw new AssertionError("Attempted to remove an edge from non-existent node(s)");
        }
        // make sure the edge exists
        if (!edgeExists(node1, node2)) {
            return this;
        }

        adjacencyList.get(node1).remove(node2);
        adjacencyList.get(node2).remove(node1);

        // remove edge from edges
        edges.removeIf(edge -> edge.edgeOfNodes(node1, node2));

        return this;
    }

    public Graph removeEdge(Edge edge) {
        // make sure the edge exists
        if (!edges.contains(edge)) {
            return this;
        }

        Node node1 = edge.getNode1();
        Node node2 = edge.getNode2();
        adjacencyList.get(node1).remove(node2);
        adjacencyList.get(node2).remove(node1);

        // remove edge from edges
        edges.removeIf(graphEdge -> edge == graphEdge);

        return this;
    }


    /**
     * Return the neighbors to a specific node in this graph
     *
     * @param node The node to find the neighbors of
     * @return A set of nodes
     */
    public HashSet<Node> getNeighbors(Node node) {
        if (!adjacencyList.containsKey(node)) {
            throw new AssertionError();
        }
        return adjacencyList.get(node);
    }

    /**
     * @return The set of all nodes
     */
    public HashSet<Node> getNodes() {
        // defensive copy of nodes
        HashSet<Node> nodes = new HashSet<>();
        adjacencyList.forEach((key, value) -> {
            nodes.add(key);
        });
        return nodes;
    }

    /**
     * @param filterFunction a filter function
     * @return a filtered set off all the nodes in the graph
     */
    public HashSet<Node> getNodes(Predicate<Node> filterFunction) {
        return getNodes().stream().filter(filterFunction).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * @param filterFunction a filter function
     * @return a filtered set off all the edges in the graph
     */
    public HashSet<Edge> getEdges(Predicate<Edge> filterFunction) {
        return edges.stream()
                .filter(filterFunction)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * @param node1 a node inside of the edge
     * @param node2 a node inside of the edge
     * @return the edge or null if it does not exist
     */
    public Edge getEdge(Node node1, Node node2) {
        return edges.stream()
                .filter(edge -> edge.hasNode(node1) && edge.hasNode(node2))
                .findFirst()
                .orElse(null);
    }

    /**
     *
     * @param node1 a node inside of the edge
     * @param node2 a node inside of the edge
     * @return if an edge exists with these nodes
     */
    public boolean edgeExists(Node node1, Node node2) {
        return edges.stream().anyMatch(edge -> edge.edgeOfNodes(node1, node2));
    }

    public boolean edgeExists(Edge edge) {
        return edges.contains(edge);
    }

}
