package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DepthSearchTest {
    @Test
    public void testDepth1(){
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

        ArrayList<Node> path1 = new ArrayList<>();
        path1.add(nodeS);
        path1.add(nodeA);
        path1.add(nodeB);
        path1.add(nodeC);
        path1.add(nodeD);
        path1.add(nodeE);
        path1.add(nodeF);

        ArrayList<Node> path2 = new ArrayList<>();
        path2.add(nodeS);
        path2.add(nodeQ);
        path2.add(nodeR);
        path2.add(nodeF);

        PathFindingAlgorithm D = new BreathSearch();
        Path actualPath = D.getPath(graph, nodeS, nodeF);
        boolean pathChecker;
        if(actualPath.equals(new Path(path1,graph))|| actualPath.equals(new Path(path2,graph))){
             pathChecker = true;
        }
        else
        {
             pathChecker = false;
        }


        // TODO @LAN, fix this
        assertEquals(true, pathChecker);
    }


    @Test
    public void testDepth2(){
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
        graph.addEdge(nodeB, nodeC);
        graph.addEdge(nodeC, nodeD);

        // create all paths for possible routes for testing
        ArrayList<Node> path1Arr = new ArrayList<>();
        path1Arr.add(nodeA);
        path1Arr.add(nodeC);
        Path path1 = new Path(path1Arr, graph);
        ArrayList<Node> path2Arr = new ArrayList<>();
        path2Arr.add(nodeA);
        path2Arr.add(nodeC);
        path2Arr.add(nodeD);
        Path path2 = new Path(path2Arr, graph);
        PathFindingAlgorithm D = new BreathSearch();
        assertEquals(path1, D.getPath(graph, nodeA, nodeC));

        graph.removeEdge(nodeA, nodeB);
        assertEquals(path2, D.getPath(graph, nodeA, nodeD));

    }
}