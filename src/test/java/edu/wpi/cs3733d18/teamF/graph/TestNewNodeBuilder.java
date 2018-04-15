package edu.wpi.cs3733d18.teamF.graph;

import org.junit.Test;

public class TestNewNodeBuilder {

    @Test(expected = AssertionError.class)
    public void testEmptyNodeCreation() {
        new NewNodeBuilder().build();
    }

}
