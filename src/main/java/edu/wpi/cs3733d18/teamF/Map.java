package edu.wpi.cs3733d18.teamF;

import edu.wpi.cs3733d18.teamF.db.DatabaseHandler;
import edu.wpi.cs3733d18.teamF.db.DatabaseItem;
import edu.wpi.cs3733d18.teamF.db.DatabaseSingleton;
import edu.wpi.cs3733d18.teamF.graph.*;
import javafx.geometry.Point2D;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Map extends Observable implements DatabaseItem, Observer {
    private DatabaseHandler dbHandler;
    private Graph graph;
    private String floor = "02";
    // TODO change to enumeration
    private boolean is2D = true;

    private boolean stairsDisabled = false;
    private boolean elevatorsDisabled = false;

    public void setPathSelector(PathFindingAlgorithm pathSelector) {
        this.pathSelector = pathSelector;
    }

    private PathFindingAlgorithm pathSelector = new AStar();

    public Map() {
        graph = new Graph();
        dbHandler = DatabaseSingleton.getInstance().getDbHandler();
    }

    public Map(DatabaseHandler dbHandler) {
        graph = new Graph();
        this.dbHandler = dbHandler;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        if (!NodeBuilder.getFloors().contains(floor)) {
            throw new AssertionError("Not a valid floor");
        }
        if (!this.floor.equals(floor)) {
            this.floor = floor;
            setChanged();
            notifyObservers();
        }
    }

    public boolean is2D() {
        return is2D;
    }

    public void setIs2D(boolean is2D) {
        this.is2D = is2D;
        setChanged();
        notifyObservers();
    }

    public void createNode(Node node) {
        try {
            // test that the node does not already exist
            if (graph.getNodes(graphNode -> graphNode == node).size() == 1) {
                return;
            }

            graph.addNode(node);
            // track this node
            node.addObserver(this);

            // will only reach here if successful node creation
            String cmd = "INSERT INTO NODE VALUES ("
                    + "'" + node.getNodeID() + "'"
                    + "," + (int) node.getPosition().getX()
                    + "," + (int) node.getPosition().getY()
                    + ",'" + node.getFloor() + "'"
                    + ",'" + node.getBuilding() + "'"
                    + ",'" + node.getNodeType() + "'"
                    + ",'" + node.getShortName() + "'"
                    + ",'" + node.getLongName() + "'"
                    + ",'" + "Team F" + "'"
                    + "," + (int) node.getWireframePosition().getX()
                    + "," + (int) node.getWireframePosition().getY()
                    + ")";
            dbHandler.runAction(cmd);
            syncCSVFromDB(dbHandler);

            setChanged();
            notifyObservers();

        } catch (AssertionError e) {
            e.printStackTrace();
        }
    }


    public void removeNode(Node node) {
        // make sure the node exists in this graph
        if (graph.getNodes(graphNode -> graphNode == node).size() == 0) {
            return;
        }

        // delete all edges with this node as an edge
        HashSet<Edge> edges = graph.getEdges(edge -> edge.hasNode(node));
        for (Edge edge : edges) {
            removeEdge(edge);
        }

        // remove the node from this graph
        graph.removeNode(node);

        // remove this node from the database
        String cmd = "DELETE FROM NODE WHERE ID='" + node.getNodeID() + "'";
        dbHandler.runAction(cmd);
        syncCSVFromDB(dbHandler);

        setChanged();
        notifyObservers();
    }

    public void addEdge(Node node1, Node node2) {
        // make sure that the nodes exist
        if (graph.getNodes(graphNode -> graphNode == node1 || graphNode == node2).size() != 2) {
            return;
        }
        // make sure the edge does not already exist
        if (graph.getEdges(edge -> edge.edgeOfNodes(node1, node2)).size() == 1) {
            return;
        }
        // make the edge
        graph.addEdge(node1, node2);

        // sync the database
        Edge edge = graph.getEdge(node1, node2);
        String cmd = "INSERT INTO EDGE VALUES ("
                + "'" + edge.getEdgeID() + "'"
                + ",(select ID from NODE where ID = '" + edge.getNode1().getNodeID() + "')"
                + ",(select ID from NODE where ID = '" + edge.getNode2().getNodeID() + "')"
                + ")";
        dbHandler.runAction(cmd);
        syncCSVFromDB(dbHandler);

        setChanged();
        notifyObservers();
    }

    public void removeEdge(Node node1, Node node2) {
        // make sure that the nodes exist
        if (graph.getNodes(graphNode -> graphNode == node1 || graphNode == node2).size() != 2) {
            return;
        }
        // make sure the edge already exists
        if (!graph.edgeExists(node1, node2)) {
            return;
        }

        // save the edge
        HashSet<Edge> edges = graph.getEdges(edge -> edge.edgeOfNodes(node1, node2));
        Edge edge = edges.iterator().next();

        removeEdge(edge);
    }

    public void removeEdge(Edge edge) {
        // verify edge exists in graph
        if (!graph.edgeExists(edge)) {
            return;
        }
        // remove the edge from the graph
        graph.removeEdge(edge);

        // remove the edge from the database
        String cmd = "DELETE FROM EDGE WHERE EDGEID='" + edge.getEdgeID() + "'";
        dbHandler.runAction(cmd);
        syncCSVFromDB(dbHandler);

        setChanged();
        notifyObservers();
    }


    public HashSet<Node> getNeighbors(Node node) {
        return graph.getNeighbors(node);
    }

    public HashSet<Node> getNodes() {
        return graph.getNodes();
    }

    public HashSet<Node> getNodes(Predicate<Node> filterFunction) {
        return graph.getNodes(filterFunction);
    }

    public HashSet<Edge> getEdges(Predicate<Edge> filterFunction) {
        return graph.getEdges(filterFunction);
    }

    public Edge getEdge(Node node1, Node node2) {
        return graph.getEdge(node1, node2);
    }

    public boolean edgeExists(Node node1, Node node2) {
        return graph.edgeExists(node1, node2);
    }

    public edu.wpi.cs3733d18.teamF.graph.Path getPath(Node node1, Node node2) {
        return pathSelector.getPath(graph, node1, node2);
    }

    public Node findNodeClosestTo(Node node, Predicate<Node> destFilter) {
        double closestDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for (Node n : getNodes(destFilter)) {
            Path path = getPath(node, n);
            if (path.getNodes().size() == 0) {
                continue;
            }
            double len = path.getLength();
            if (len < closestDistance) {
                closestDistance = len;
                closestNode = n;
            }
        }
        return closestNode;
    }

    //Note: This function gets you the closest node on the specified floor. Don't use this if you don't know what floor you're looking for!
    public Node findNodeClosestTo(double x1, double y1) {
        return findNodeClosestTo(x1, y1, true);
    }

    //Note: This function gets you the closest node on the specified floor. Don't use this if you don't know what floor you're looking for!
    public Node findNodeClosestTo(double x1, double y1, boolean is2D) {
        return findNodeClosestTo(x1, y1, is2D, Objects::nonNull);
    }

    public Node findNodeClosestTo(Node node, HashSet<Node> nodes){
        double closestDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for (Node n : nodes) {
            Path path = getPath(node, n);
            if (path.getNodes().size() == 0) {
                continue;
            }
            double len = path.getLength();
            if (len < closestDistance) {
                closestDistance = len;
                closestNode = n;
            }
        }
        return closestNode;
    }

    //Note: This function gets you the closest node on the specified floor. Don't use this if you don't know what floor you're looking for!
    public Node findNodeClosestTo(double x1, double y1, boolean is2D, Predicate<Node> destFilter) {
        double closestDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for (Node n : getNodes(destFilter)) {
            double x2 = n.getPosition().getX();
            double y2 = n.getPosition().getY();
            if (!is2D) {
                x2 = n.getWireframePosition().getX();
                y2 = n.getWireframePosition().getY();
            }
            double distance = Math.sqrt(((x2 - x1) * (x2 - x1)) + (y2 - y1) * (y2 - y1));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestNode = n;
            }
        }
        return closestNode;
    }

    public void disableStairs() {

        if (!stairsDisabled) {
            HashSet<Node> Nodes = graph.getNodes();
            stairsDisabled = true;

            for (Node n : Nodes) {
                if (n.getNodeType().equals("STAI")) {
                    n.setAdditionalWeight(n.getAdditionalWeight() + 5000);
                }
            }
        }
    }

    public void enableStairs() {

        if (stairsDisabled) {
            HashSet<Node> nodes = graph.getNodes();
            stairsDisabled = false;

            for (Node n : nodes) {
                if (n.getNodeType().equals("STAI")) {
                    n.setAdditionalWeight(n.getAdditionalWeight() - 5000);
                }
            }
        }
    }

    public void disableElevators() {

        if (!elevatorsDisabled) {
            HashSet<Node> nodes = graph.getNodes();
            elevatorsDisabled = true;

            for (Node n : nodes) {
                if (n.getNodeType().equals("ELEV")) {
                    n.setAdditionalWeight(n.getAdditionalWeight() + 5000);
                }
            }
        }
    }

    public void enableElevators() {

        if (elevatorsDisabled) {
            HashSet<Node> nodes = graph.getNodes();
            elevatorsDisabled = false;

            for (Node n : nodes) {
                if (n.getNodeType().equals("ELEV")) {
                    n.setAdditionalWeight(n.getAdditionalWeight() - 5000);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Node)) {
            return;
        }
        Node node = (Node) o;

        // if arg is null the nodeID did not change and just update the node properties
        if (arg == null) {
            String cmd = "UPDATE NODE "
                    + "SET X_COORD = " + node.getPosition().getX()
                    + ", Y_COORD = " + node.getPosition().getY()
                    + ", BUILDING = '" + node.getBuilding() + "'"
                    + ", SHORTNAME = '" + node.getShortName() + "'"
                    + ", LONGNAME = '" + node.getLongName() + "'"
                    + ", XCOORD3D = " + node.getWireframePosition().getX()
                    + ", YCOORD3D = " + node.getWireframePosition().getY()
                    + " WHERE ID='" + node.getNodeID() + "'";
            dbHandler.runAction(cmd);
            syncCSVFromDB(dbHandler);

            setChanged();
            notifyObservers();
        }
        // if arg == String, the nodeID and all edgeIDs have to be updated in the database
        else {
            String newNodeID = (String) arg;
            if (newNodeID.equals(node.getNodeID())) {
                return;
            }

            // save all neighbors
            HashSet<Node> neighbors = new HashSet<>(graph.getNeighbors(node));
            // get the edges that have to be changed
            HashSet<Edge> edges = neighbors.stream()
                    .map(neighborNode -> graph.getEdge(node, neighborNode))
                    .collect(Collectors.toCollection(HashSet::new));

            // remove all edges
            for (Edge edge : edges) {
                removeEdge(edge);
            }
            // remove the old node
            removeNode(node);

            // create a new node
            Node newNode = new ExistingNodeBuilder()
                    .setPosition(node.getPosition())
                    .setBuilding(node.getBuilding())
                    .setNodeID(newNodeID)
                    .setShortName(node.getShortName())
                    .setWireframePosition(node.getWireframePosition())
                    .build();
            createNode(newNode);
            newNode.setAdditionalWeight(node.getAdditionalWeight());
            // reconnect the edges
            for (Node neighbor : neighbors) {
                addEdge(newNode, neighbor);
            }
            setChanged();
            notifyObservers();
        }
    }


    ////////////////////////////////////////////////////////////////
    //                                                            //
    //               DATABASE SYNCHRONIZATION                     //
    //                                                            //
    ////////////////////////////////////////////////////////////////

    @Override
    public void initDatabase(DatabaseHandler dbHandler) {
        try {
            //if the table does not yet exist in the db, initialize it
            if (!dbHandler.tableExists("NODE")) {
                System.out.println("DB: Initializing NODE table entry");
                dbHandler.runSQLScript("init_node_db.sql");

                List<String> nodeFilePaths;
                boolean existingCompiledNodeFile = false;
                // see if there is an already compiled nodes csv file
                if (new File("map/nodes.csv").exists()) {
                    nodeFilePaths = new LinkedList<>(Collections.singletonList("map/nodes.csv"));
                    existingCompiledNodeFile = true;

                } else {
                    nodeFilePaths = Files.walk(Paths.get("initMap"))
                            .filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().contains("odes.csv"))
                            .map(path -> "initMap/" + path.getFileName().toString())
                            .collect(Collectors.toList());
                }

                for (String nodeFilePath : nodeFilePaths) {
                    File csvFile;
                    if (existingCompiledNodeFile) {
                        System.out.println("Missing database, Loading Nodes from map/nodes.csv");
                        csvFile = new File(nodeFilePath);
                    } else {
                        System.out.println("Missing database, generating Nodes from initMap/*odes.csv files.");
                        csvFile = new File(nodeFilePath);
                    }

                    CSVParser parser = CSVParser.parse(csvFile, StandardCharsets.UTF_8, CSVFormat.RFC4180);

                    for (CSVRecord record : parser) {
                        if (record.get(0).contains("nodeID")) {
                            continue;
                        }
                        String name = record.get(0);
                        int x = (int) Double.parseDouble(record.get(1));
                        int y = (int) Double.parseDouble(record.get(2));
                        String floor = record.get(3);
                        floor = floor.replace(" ", "");
                        if (!floor.contains("L") && !floor.contains("G")) {
                            floor = String.format("%02d", Integer.parseInt(floor));
                        }
                        String building = record.get(4);
                        String nodeType = record.get(5);
                        String longName = record.get(6);
                        String shortName = record.get(7);
                        String teamName = record.get(8);
                        int x3d = (int) Double.parseDouble(record.get(9));
                        int y3d = (int) Double.parseDouble(record.get(10));

                        String cmd = "INSERT INTO NODE VALUES ("
                                + "'" + name + "'"
                                + "," + x
                                + "," + y
                                + ",'" + floor + "'"
                                + ",'" + building + "'"
                                + ",'" + nodeType + "'"
                                + ",'" + longName + "'"
                                + ",'" + shortName + "'"
                                + ",'" + teamName + "'"
                                + "," + x3d
                                + "," + y3d
                                + ")";
                        dbHandler.runAction(cmd);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        try {
            //if the table does not yet exist in the db, initialize it
            if (!dbHandler.tableExists("EDGE")) {
                System.out.println("DB: Initializing EDGE table entry");
                dbHandler.runSQLScript("init_edge_db.sql");

                List<String> edgeFilePaths;
                boolean existingCompiledEdgeFile = false;
                // see if there is an already compiled nodes csv file
                if (new File("map/edges.csv").exists()) {
                    edgeFilePaths = new LinkedList<>(Collections.singletonList("map/edges.csv"));
                    existingCompiledEdgeFile = true;

                } else {
                    edgeFilePaths = Files.walk(Paths.get("initMap"))
                            .filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().contains("dges.csv"))
                            .map(path -> path.getFileName().toString())
                            .map(path -> "initMap/" + path)
                            .collect(Collectors.toList());
                }

                for (String edgeFilePath : edgeFilePaths) {
                    File csvFile;
                    if (existingCompiledEdgeFile) {
                        System.out.println("Missing database, Loading Edges from map/edges.csv");
                        csvFile = new File(edgeFilePath);
                    } else {
                        System.out.println("Missing database, generating Edges from db/map/*dges.csv files.");
                        csvFile = new File(edgeFilePath);
                    }

                    CSVParser parser = CSVParser.parse(csvFile, StandardCharsets.UTF_8, CSVFormat.RFC4180);

                    for (CSVRecord record : parser) {
                        if (record.get(0).contains("edgeID")) {
                            continue;
                        }

                        String edgeID = record.get(0);
                        String startNode = record.get(1);
                        String endNode = record.get(2);

                        String cmd = "INSERT INTO EDGE VALUES ("
                                + "'" + edgeID + "'"
                                + ",(select ID from NODE where ID = '" + startNode + "')"
                                + ",(select ID from NODE where ID = '" + endNode + "')"
                                + ")";
                        dbHandler.runAction(cmd);
                    }
                }
            }
        } catch (IOException | SQLException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public LinkedList<String> getTableNames() {
        return new LinkedList<>(Arrays.asList("NODE", "EDGE"));
    }

    @Override
    public void syncLocalFromDB(String tableName, ResultSet resultSet) {
        try {
            switch (tableName) {
                case "NODE": {
                    while (resultSet.next()) {
                        int height;
                        String floor = resultSet.getString(1).substring(8);

                        // extract data from database
                        String nodeID = resultSet.getString(1);
                        String longName = resultSet.getString(7);
                        String shortName = resultSet.getString(8);
                        String building = resultSet.getString(5);
                        Point2D position = new Point2D(Double.parseDouble(resultSet.getString(2))
                                , Double.parseDouble(resultSet.getString(3)));
                        Point2D wireframePosition = new Point2D(Double.parseDouble(resultSet.getString(10))
                                , Double.parseDouble(resultSet.getString(11)));

                        // construct node
                        Node newNode = new ExistingNodeBuilder()
                                .setNodeID(nodeID)
                                .setShortName(shortName)
                                .setLongName(longName)
                                .setPosition(position)
                                .setWireframePosition(wireframePosition)
                                .setBuilding(building)
                                .build();

                        // add to graph
                        graph.addNode(newNode);
                        // track this new node
                        newNode.addObserver(this);
                    }
                }
                break;
                case "EDGE": {
                    while (resultSet.next()) {
                        String edgeID = resultSet.getString(1);
                        String node1Name = resultSet.getString(2);
                        String node2Name = resultSet.getString(3);
                        try {
                            Node node1 = graph.getNodes(node -> node.getNodeID().equals(node1Name)).iterator().next();
                            Node node2 = graph.getNodes(node -> node.getNodeID().equals(node2Name)).iterator().next();
                            graph.addEdge(node1, node2, edgeID);
                        } catch (NoSuchElementException e) {
                            System.out.println("[Warning] Uninitialized edge");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syncCSVFromDB(DatabaseHandler dbHandler) {
        try {
            new File("map").mkdirs();

            // save nodes
            FileWriter fw = new FileWriter("map/nodes.csv", false);
            CSVPrinter csvPrinterNodes = new CSVPrinter(fw, CSVFormat.DEFAULT
                    .withHeader("nodeID", "xcoord", "ycoord", "floor", "building", "nodeType", "longName"
                            , "shortName", "teamAssigned", "xcoord3d", "ycoord3d"));
            ResultSet nodeSet = dbHandler.runQuery("SELECT * FROM NODE");
            while (nodeSet.next()) {
                String floor = nodeSet.getString(4);
                floor = floor.replace(" ", "");
                if (!floor.contains("L") && !floor.contains("G")) {
                    floor = String.format("%02d", Integer.parseInt(floor));
                }

                csvPrinterNodes.printRecord(
                        nodeSet.getString(1)
                        , nodeSet.getString(2)
                        , nodeSet.getString(3)
                        , floor
                        , nodeSet.getString(5)
                        , nodeSet.getString(6)
                        , nodeSet.getString(7)
                        , nodeSet.getString(8)
                        , nodeSet.getString(9)
                        , nodeSet.getString(10)
                        , nodeSet.getString(11));
            }
            csvPrinterNodes.flush();
            nodeSet.close();
            fw.close();

            // save edges
            fw = new FileWriter("map/edges.csv", false);
            CSVPrinter csvPrinterEdges = new CSVPrinter(fw, CSVFormat.DEFAULT
                    .withHeader("edgeID", "startNode", "endNode"));
            ResultSet edgeSet = dbHandler.runQuery("SELECT * FROM EDGE");
            while (edgeSet.next()) {
                csvPrinterEdges.printRecord(
                        edgeSet.getString(1)
                        , edgeSet.getString(2)
                        , edgeSet.getString(3));
            }
            csvPrinterEdges.flush();
            edgeSet.close();
            fw.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
