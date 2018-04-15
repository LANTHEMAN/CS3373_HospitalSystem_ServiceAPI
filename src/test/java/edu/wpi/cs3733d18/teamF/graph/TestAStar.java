package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class TestAStar {

    @Test
    public void testAStar1() {
        Node nodeA = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(0, 0))
                .setNodeType("HALL")
                .forceNumNodeType(1)
                .setBuilding("Place")
                .build();
        Node nodeB = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(1, 1))
                .setNodeType("HALL")
                .forceNumNodeType(2)
                .setBuilding("Place")
                .build();
        Node nodeC = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(4, 4))
                .setNodeType("HALL")
                .forceNumNodeType(3)
                .setBuilding("Place")
                .build();
        Node nodeD = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, 2))
                .setNodeType("HALL")
                .forceNumNodeType(4)
                .setBuilding("Place")
                .build();

        Graph graph = new Graph();
        graph.addNode(nodeA).addNode(nodeB).addNode(nodeC).addNode(nodeD);

        graph.addEdge(nodeA, nodeB).addEdge(nodeA, nodeC);
        graph.addEdge(nodeB, nodeC).addEdge(nodeB, nodeD);
        graph.addEdge(nodeC, nodeD);

        // create all paths for possible routes for testing
        ArrayList<Node> path1Arr = new ArrayList<>();
        path1Arr.add(nodeA);
        path1Arr.add(nodeB);
        path1Arr.add(nodeD);
        Path path1 = new Path(path1Arr, graph);
        ArrayList<Node> path2Arr = new ArrayList<>();
        path2Arr.add(nodeA);
        path2Arr.add(nodeC);
        path2Arr.add(nodeD);
        Path path2 = new Path(path2Arr, graph);
        PathFindingAlgorithm A = new AStar();
        assertEquals(path1, A.getPath(graph, nodeA, nodeD));

        graph.removeEdge(nodeA, nodeB);
        assertEquals(path2, A.getPath(graph, nodeA, nodeD));

        graph.addEdge(nodeA, nodeB);
        assertEquals(path1, A.getPath(graph, nodeA, nodeD));

        graph.removeNode(nodeB);
        assertEquals(path2, A.getPath(graph, nodeA, nodeD));

        // check that filter methods work for nodes
        graph.addNode(nodeB);
        HashSet<Node> filteredNodes = graph.getNodes(
                node -> node.getPosition().distance(0.d, 0.d) < 2);
        HashSet<Node> closeToOrigin = new HashSet<>();
        closeToOrigin.add(nodeA);
        closeToOrigin.add(nodeB);
        assertEquals(closeToOrigin, filteredNodes);

        // check that filter methods work for edges
        HashSet<Edge> filteredEdges = graph.getEdges(edge -> edge.hasNode(nodeD));
        HashSet<Edge> connectedToNodeD = new HashSet<>();
        connectedToNodeD.add(graph.getEdge(nodeC, nodeD));
        assertEquals(connectedToNodeD, filteredEdges);
    }

    @Test
    public void testAStar2() {
        Node nodeS = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(0, 0))
                .setNodeType("HALL")
                .forceNumNodeType(1)
                .setBuilding("Place")
                .build();
        Node nodeA = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(0, -1))
                .setNodeType("HALL")
                .forceNumNodeType(2)
                .setBuilding("Place")
                .build();
        Node nodeB = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(1, -1))
                .setNodeType("HALL")
                .forceNumNodeType(3)
                .setBuilding("Place")
                .build();
        Node nodeC = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(1, 0))
                .setNodeType("HALL")
                .forceNumNodeType(4)
                .setBuilding("Place")
                .build();
        Node nodeD = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, 0))
                .setNodeType("HALL")
                .forceNumNodeType(5)
                .setBuilding("Place")
                .build();
        Node nodeE = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, -1))
                .setNodeType("HALL")
                .forceNumNodeType(6)
                .setBuilding("Place")
                .build();
        Node nodeQ = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(-0.5, 0))
                .setNodeType("HALL")
                .forceNumNodeType(7)
                .setBuilding("Place")
                .build();
        Node nodeR = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(-1, 0))
                .setNodeType("HALL")
                .forceNumNodeType(8)
                .setBuilding("Place")
                .build();
        Node nodeF = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, -2))
                .setNodeType("HALL")
                .forceNumNodeType(9)
                .setBuilding("Place")
                .build();

        Graph graph = new Graph();
        graph.addNode(nodeS).addNode(nodeA).addNode(nodeB).addNode(nodeC).addNode(nodeD).addNode(nodeE)
                .addNode(nodeQ).addNode(nodeR).addNode(nodeF);

        graph.addEdge(nodeS, nodeA).addEdge(nodeS, nodeQ)
                .addEdge(nodeA, nodeB)
                .addEdge(nodeB, nodeC)
                .addEdge(nodeC, nodeD)
                .addEdge(nodeD, nodeE)
                .addEdge(nodeE, nodeF)
                .addEdge(nodeQ, nodeR)
                .addEdge(nodeR, nodeF);

        ArrayList<Node> path = new ArrayList<>();
        path.add(nodeS);
        path.add(nodeQ);
        path.add(nodeR);
        path.add(nodeF);
        PathFindingAlgorithm A = new AStar();
        assertEquals(new Path(path, graph), A.getPath(graph, nodeS, nodeF));
    }

    @Test
    public void testAStar3WithBlock() {
        Node nodeS = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(0, 0))
                .setNodeType("HALL")
                .forceNumNodeType(1)
                .setBuilding("Place")
                .build();
        Node nodeA = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(0, -1))
                .setNodeType("HALL")
                .forceNumNodeType(2)
                .setBuilding("Place")
                .build();
        Node nodeB = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(1, -1))
                .setNodeType("HALL")
                .forceNumNodeType(3)
                .setBuilding("Place")
                .build();
        Node nodeC = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(1, 0))
                .setNodeType("HALL")
                .forceNumNodeType(4)
                .setBuilding("Place")
                .build();
        Node nodeD = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, 0))
                .setNodeType("HALL")
                .forceNumNodeType(5)
                .setBuilding("Place")
                .build();
        Node nodeE = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, -1))
                .setNodeType("HALL")
                .forceNumNodeType(6)
                .setBuilding("Place")
                .build();
        Node nodeQ = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(-1, 0))
                .setNodeType("HALL")
                .forceNumNodeType(7)
                .setBuilding("Place")
                .build();
        Node nodeR = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(-1, 0))
                .setNodeType("HALL")
                .forceNumNodeType(8)
                .setBuilding("Place")
                .build();
        Node nodeF = new NewNodeBuilder()
                .setFloor("0G")
                .setPosition(new Point2D(2, -2))
                .setNodeType("HALL")
                .forceNumNodeType(9)
                .setBuilding("Place")
                .build();

        Graph graph = new Graph();
        graph.addNode(nodeS).addNode(nodeA).addNode(nodeB).addNode(nodeC).addNode(nodeD).addNode(nodeE)
                .addNode(nodeQ).addNode(nodeR).addNode(nodeF);

        graph.addEdge(nodeS, nodeA).addEdge(nodeS, nodeQ)
                .addEdge(nodeA, nodeB)
                .addEdge(nodeB, nodeC)
                .addEdge(nodeC, nodeD)
                .addEdge(nodeD, nodeE)
                .addEdge(nodeE, nodeF)
                .addEdge(nodeQ, nodeR)
                .addEdge(nodeR, nodeF);

        ArrayList<Node> path = new ArrayList<>();
        path.add(nodeS);
        path.add(nodeA);
        path.add(nodeB);
        path.add(nodeC);
        path.add(nodeD);
        path.add(nodeE);
        path.add(nodeF);

        ArrayList<Node> path2 = new ArrayList<>();
        path2.add(nodeS);
        path2.add(nodeQ);
        path2.add(nodeR);
        path2.add(nodeF);
        PathFindingAlgorithm A = new AStar();
        assertEquals(new Path(path2, graph), A.getPath(graph, nodeS, nodeF));

        // now 'block' a specific path by adding additional weight

        nodeQ.setAdditionalWeight(3);

        assertEquals(new Path(path, graph), A.getPath(graph, nodeS, nodeF));
    }

}
