package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Test;

import static org.junit.Assert.*;

public class EdgeTest {
    private static final Node node1 = new NewNodeBuilder()
            .setNodeType("HALL")
            .forceNumNodeType(0)
            .setFloor("0G")
            .setBuilding("Home")
            .setPosition(new Point2D(0, 0))
            .setShortName("place 1").build();

    private static final Node node2 = new NewNodeBuilder()
            .setNodeType("HALL")
            .forceNumNodeType(1)
            .setFloor("0G")
            .setBuilding("Home")
            .setPosition(new Point2D(1, 0))
            .setShortName("place 2").build();

    Edge edge1 = new Edge(node1,node2,"testID");

    @Test
    public void getEdgeID() {
        assertEquals("testID",edge1.getEdgeID());

    }

    @Test
    public void hasNode() {
       assertEquals(true,edge1.hasNode(node1));
    }

    @Test
    public void edgeOfNodes() {
        assertEquals(true,edge1.edgeOfNodes(node1,node2));
    }

    @Test
    public void getNode1() {
        assertEquals(node1,edge1.getNode1());
    }

    @Test
    public void getNode2() {
        assertEquals(node2,edge1.getNode2());
    }

    @Test
    public void getDistance() {
        assertEquals(1.0,edge1.getDistance(),0.1);
    }
}