package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Test;

public class TestGraph {

    private static final Node node1 = new NewNodeBuilder()
            .setNodeType("HALL")
            .forceNumNodeType(0)
            .setFloor("0G")
            .setBuilding("Home")
            .setPosition(new Point2D(0,0))
            .setShortName("place 1").build();

    private static final Node node2 = new NewNodeBuilder()
            .setNodeType("HALL")
            .forceNumNodeType(1)
            .setFloor("0G")
            .setBuilding("Home")
            .setPosition(new Point2D(1,0))
            .setShortName("place 2").build();

    @Test(expected = AssertionError.class)
    public void testRemoveEdgeWithNonExistingNode() {
        Graph graph = new Graph();
        graph.addNode(node1);
        graph.removeEdge(node1, node2);
    }

}
