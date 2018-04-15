package edu.wpi.cs3733d18.teamF.db;

import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DummyGraph implements DatabaseItem {

    public HashMap<String, Pair<DummyNode, LinkedList<DummyNode>>> nodes = new HashMap<>();
    public String nodesFile = "TestNodes.csv";
    public String edgeFile_in   = "TestEdges.csv";
    public String edgeFile_out   = "TestEdges.csv";

    @Override
    public void initDatabase(DatabaseHandler dbHandler) {
        try {
            //if the table does not yet exist in the db, initialize it
            if (!dbHandler.tableExists("NODE")) {
                dbHandler.runSQLScript("init_node_db.sql");

                // TODO make into a function
                File csvFile = new File(getClass().getResource(nodesFile).toURI().getPath());
                CSVParser parser = CSVParser.parse(csvFile, StandardCharsets.UTF_8, CSVFormat.RFC4180);

                for (CSVRecord record : parser) {
                    if (record.get(0).equals("nodeID")) {
                        continue;
                    }
                    String name = record.get(0);
                    double x = Double.parseDouble(record.get(1));
                    double y = Double.parseDouble(record.get(2));
                    String floor = record.get(3);
                    String building = record.get(4);
                    String nodeType = record.get(5);
                    String longName = record.get(6);
                    String shortName = record.get(7);

                    String query = "INSERT INTO NODE VALUES ("
                            + "'" + name + "',"
                            + x + ","
                            + y + ","
                            + "'" + floor + "',"
                            + "'" + building + "',"
                            + "'" + nodeType + "',"
                            + "'" + longName + "',"
                            + "'" + shortName + "'"
                            + ")";
                    dbHandler.runAction(query);
                }
            }

            // load edges into DB
            if (!dbHandler.tableExists("EDGE")) {
                dbHandler.runSQLScript("init_edge_db.sql");
            }

        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // initialize local copy from database
        dbHandler.syncLocalFromDB(this);
    }

    @Override
    public LinkedList<String> getTableNames() {
        return new LinkedList<>(Arrays.asList("NODE", "EDGE"));
    }

    @Override
    public void syncLocalFromDB(String tableName, ResultSet resultSet) {
        try {
            // first sync nodes
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                DummyNode node = nodes.containsKey(id) ? nodes.get(id).getKey() : new DummyNode();

                node.id = resultSet.getString(1);
                node.x = Double.parseDouble(resultSet.getString(2));
                node.y = Double.parseDouble(resultSet.getString(3));
                node.floor = resultSet.getString(4);
                node.building = resultSet.getString(5);
                node.nodeType = resultSet.getString(6);
                node.longName = resultSet.getString(7);
                node.shortName = resultSet.getString(8);

                nodes.put(id, new Pair<>(node, new LinkedList<>()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void syncDBFromLocal(DatabaseHandler dbHandler) {
        // clear old table
        dbHandler.runAction("TRUNCATE TABLE EDGE");
        dbHandler.runAction("TRUNCATE TABLE NODE");

        // update node table
        for (Map.Entry<String, Pair<DummyNode, LinkedList<DummyNode>>> nodeData : nodes.entrySet()) {
            DummyNode node = nodeData.getValue().getKey();
            // update node
            String cmd = "INSERT INTO NODE VALUES ("
                    + "'" + node.id + "',"
                    + node.x + ","
                    + node.y + ","
                    + "'" + node.floor + "',"
                    + "'" + node.building + "',"
                    + "'" + node.nodeType + "',"
                    + "'" + node.longName + "',"
                    + "'" + node.shortName + "'"
                    + ")";
            dbHandler.runAction(cmd);
        }
        //TODO update edge table
    }

    @Override
    public void syncCSVFromDB(DatabaseHandler dbHandler) {
        try {
            new File("temp/test-output").mkdirs();
            FileWriter fw = new FileWriter("temp/test-output/" + nodesFile, false);

            CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT
                    .withHeader("nodeID", "xcoord", "ycoord", "floor", "building", "nodeType", "longName", "shortName", "teamAssigned"));

            ResultSet nodeSet = dbHandler.runQuery("SELECT * FROM NODE");
            while (nodeSet.next()) {
                csvPrinter.printRecord(
                        nodeSet.getString(1)
                        , nodeSet.getString(2)
                        , nodeSet.getString(3)
                        , nodeSet.getString(4)
                        , nodeSet.getString(5)
                        , nodeSet.getString(6)
                        , nodeSet.getString(7)
                        , nodeSet.getString(8)
                        // TODO save teamAssigned
                        //, nodeSet.getString(9)
                );
            }
            csvPrinter.flush();
            nodeSet.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
