package edu.wpi.cs3733d18.teamF.graph;

import javafx.geometry.Point2D;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestExistingNodeBuilder {

    @Test(expected = AssertionError.class)
    public void testEmptyNodeCreation() {
        new ExistingNodeBuilder().build();
    }

    @Test
    public void testValidNodeID() {
        new ExistingNodeBuilder()
                .setNodeID("FHALL001L2")
                .setBuilding("Place")
                .setPosition(new Point2D(0, 0))
                .setShortName("Place in place")
                .setLongName("cool")
                .setWireframePosition(new Point2D(0, 0))
                .build();
        // will only reach here if no exception was thrown
        assertEquals(1, 1);
    }

    @Test(expected = AssertionError.class)
    public void testInvalidNodeID() {
        new ExistingNodeBuilder()
                .setNodeID("FHELL001L2")
                .setBuilding("Place")
                .setPosition(new Point2D(0, 0))
                .setShortName("Place in place")
                .setWireframePosition(new Point2D(0, 0))
                .build();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidNodeID2() {
        new ExistingNodeBuilder()
                .setNodeID("FHALL001QQ")
                .setBuilding("Place")
                .setPosition(new Point2D(0, 0))
                .setShortName("Place in place")
                .setWireframePosition(new Point2D(0, 0))
                .build();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidNodeID3() {
        new ExistingNodeBuilder()
                .setNodeID("FHALL001L")
                .setBuilding("Place")
                .setPosition(new Point2D(0, 0))
                .setShortName("Place in place")
                .setWireframePosition(new Point2D(0, 0))
                .build();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidNodeID4() {
        new ExistingNodeBuilder()
                .setNodeID("FHALL001L1")
                .setBuilding("Place")
                .setPosition(new Point2D(0, 0))
                .setShortName("Place in place")
                .build();
    }
}
