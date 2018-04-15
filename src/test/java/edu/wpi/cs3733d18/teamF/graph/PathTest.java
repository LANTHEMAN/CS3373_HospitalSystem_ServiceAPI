package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class PathTest {
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

    @Test
    public void getLength1() {


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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeS);
        pathList.add(nodeQ);
        pathList.add(nodeR);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        assertEquals(4.6,path.getLength(),0.1);
    }



    @Test
    public void getLength2() {
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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeA);
        pathList.add(nodeB);
        pathList.add(nodeC);
        pathList.add(nodeD);
        pathList.add(nodeE);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        assertEquals(5,path.getLength(),0.1);
    }


    @Test
    public void equals1() {
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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeA);
        pathList.add(nodeB);
        pathList.add(nodeC);
        pathList.add(nodeD);
        pathList.add(nodeE);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        assertEquals(false,path.equals(new double[1]));
    }

    @Test
    public void equals2() {
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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeA);
        pathList.add(nodeB);
        pathList.add(nodeC);
        pathList.add(nodeD);
        pathList.add(nodeE);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        assertEquals(true, path.equals(path));
    }

    @Test
    public void makeTextDirections() {
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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeA);
        pathList.add(nodeB);
        pathList.add(nodeC);
        pathList.add(nodeD);
        pathList.add(nodeE);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        ArrayList<String> dir = path.makeTextDirections();
        System.out.println(dir);
    }

    @Test
    public void makeTextDirections2(){
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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeS);
        pathList.add(nodeQ);
        pathList.add(nodeR);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        ArrayList<String> dir = path.makeTextDirections();
        System.out.println(dir);
    }

    @Test
    public void getAngle() {
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

        ArrayList<Node> pathList = new ArrayList<>();
        pathList.add(nodeA);
        pathList.add(nodeB);
        pathList.add(nodeC);
        pathList.add(nodeD);
        pathList.add(nodeE);
        pathList.add(nodeF);
        Path path = new Path(pathList, graph);
        assertEquals(90.0,path.getAngle(nodeS,nodeA,nodeB),0.1);
    }
}