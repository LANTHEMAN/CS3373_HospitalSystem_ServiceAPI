package edu.wpi.cs3733d18.teamF.db;

import edu.wpi.cs3733d18.teamF.graph.ExistingNodeBuilder;
import edu.wpi.cs3733d18.teamF.graph.Node;
import javafx.geometry.Point2D;
import javafx.util.Pair;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DatabaseHandlerTest {
    private int rowCount(DatabaseHandler dbHandler, String tableName) {
        ResultSet rs = dbHandler.runQuery("SELECT * FROM " + tableName);
        int rowCount = 0;
        // Process the row.
        try {
            while (rs.next()) {
                rowCount++;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount;
    }

    @Test
    public void dummyTest() {
        // delete old test database folder
        try {
            if(Files.exists(Paths.get("temp/TestDB"))){
                Files.walk(Paths.get("temp/TestDB"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseHandler dbHandler = new DatabaseHandler("temp/TestDB");
        DummyGraph graph = new DummyGraph();

        dbHandler.trackAndInitItem(graph);

        assertEquals(1, rowCount(dbHandler, "NODE"));

        assertTrue(graph.nodes.containsKey("FHALL00101"));
        assertEquals(graph.nodes.size(), 1);

        DummyNode dummyNode = new DummyNode();
        dummyNode.id = "FHALL00201";
        dummyNode.x = 2026;
        dummyNode.y = 910;
        dummyNode.floor = "1";
        dummyNode.building = "Tower";
        dummyNode.nodeType = "HALL";
        dummyNode.shortName = "Hallway F00201";
        dummyNode.longName = "Lobby Shattuck Street";

        graph.nodes.put(dummyNode.id, new Pair<>(dummyNode, new LinkedList<>()));

        graph.syncDBFromLocal(dbHandler);

        assertEquals(2, rowCount(dbHandler, "NODE"));

        dbHandler.syncCSVFromDB(graph);

        dbHandler.disconnectFromDatabase();
    }

    @Test
    public void dummyTest2() {
        // delete old test database folder
        try {
            if(Files.exists(Paths.get("temp/RealTest2"))) {
                Files.walk(Paths.get("temp/RealTest2"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseHandler dbHandler = new DatabaseHandler("temp/RealTest2");
        DummyGraph graph = new DummyGraph();
        graph.nodesFile = "MapBnodes.csv";
        dbHandler.trackAndInitItem(graph);

        assertEquals(70, rowCount(dbHandler, "NODE"));

        assertTrue(graph.nodes.containsKey("BDEPT00702"));
        DummyNode firstNode = graph.nodes.get("BDEPT00702").getKey();
        firstNode.x = 1;
        firstNode.y = 7777777;
        assertEquals(graph.nodes.size(), 70);

        DummyNode dummyNode = new DummyNode();
        dummyNode.id = "FHALL00202";
        dummyNode.x = 7878;
        dummyNode.y = 9999;
        dummyNode.floor = "1";
        dummyNode.building = "Tower";
        dummyNode.nodeType = "HALL";
        dummyNode.shortName = "Hallway F00201";
        dummyNode.longName = "This project is soo much fun! /s/s/s/s";


        graph.nodes.put(dummyNode.id, new Pair<>(dummyNode, new LinkedList<>()));

        graph.syncDBFromLocal(dbHandler);

        assertEquals(71, rowCount(dbHandler, "NODE"));

        dbHandler.syncCSVFromDB(graph);

        dbHandler.disconnectFromDatabase();
    }



    @Test
    public void dummyTest3(){
        // delete old test database folder
        try {
            if(Files.exists(Paths.get("temp/RealTest"))) {
                Files.walk(Paths.get("temp/RealTest"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseHandler dbHandler = new DatabaseHandler("temp/RealTest");
        Node nodeA = new ExistingNodeBuilder()
                .setNodeID("FHALL001L2")
                .setBuilding("Place")
                .setLongName("placeeee")
                .setPosition(new Point2D(0, 0))
                .setShortName("Place in place")
                .setWireframePosition(new Point2D(0, 0))
                .build();

//        LanguageInterpreter s = new LanguageInterpreter("Language Interpreter", nodeA, "Test 1", "Incomplete", "German", 0);
  //      s.parseIntoDescription();

    //    ServiceRequestSingleton ps = ServiceRequestSingleton.getInstance(dbHandler);
      //  ps.sendServiceRequest(s);


    }
}
