package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestNode {

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

    @Test
    public void testDisplacementTo() {
        Assert.assertEquals(1.0, node1.displacementTo(node2), 0.001);
    }

    @Test
    public void getHeight() {
        assertEquals(300,node1.getHeight("03"));

    }

    @Test
    public void displacementTo() {
        assertEquals(1,node1.displacementTo(node2),0.1);
    }


}
