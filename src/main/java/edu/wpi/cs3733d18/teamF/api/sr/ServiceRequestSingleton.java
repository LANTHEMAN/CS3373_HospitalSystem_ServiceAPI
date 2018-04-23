package edu.wpi.cs3733d18.teamF.api.sr;

import edu.wpi.cs3733d18.teamF.api.controller.UserSingleton;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseHandler;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseItem;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseSingleton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class ServiceRequestSingleton implements DatabaseItem {
    private static ServiceRequestSingleton ourInstance = new ServiceRequestSingleton();
    // just for testing
    private static HashMap<DatabaseHandler, ServiceRequestSingleton> testDatabases = new HashMap<>();
    private DatabaseHandler dbHandler;
    private ArrayList<ServiceRequests> listOfRequests = new ArrayList<>();
    private int id = 0;
    private ServiceRequests popUpRequest;
    private String lastFilter;
    private String lastSearch;
    private int prefWidth;
    private int prefLength;
    private String destNodeID;

    private ServiceRequestSingleton() {
        // initialize this class with the database
        dbHandler = DatabaseSingleton.getInstance().getDbHandler();
        dbHandler.trackAndInitItem(this);


        int max = -1;
        String sql = "SELECT MAX(ID) FROM SERVICEREQUEST";
        ResultSet rs = dbHandler.runQuery(sql);
        try {
            if (rs.next()) {
                max = rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            max = -1;
        }

        id = max + 1;
    }

    private ServiceRequestSingleton(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
        dbHandler.trackAndInitItem(this);
    }

    public static ServiceRequestSingleton getInstance() {
        return ourInstance;
    }

    public static ServiceRequestSingleton getInstance(DatabaseHandler dbHandler) {
        if (testDatabases.containsKey(dbHandler)) {
            return testDatabases.get(dbHandler);
        }
        testDatabases.put(dbHandler, new ServiceRequestSingleton(dbHandler));
        return testDatabases.get(dbHandler);
    }

    public int generateNewID() {
        return id++;
    }

    public void updateStatus(ServiceRequests s) {
        String sql = "UPDATE ServiceRequest SET status = '" + s.getStatus() + "' WHERE id = " + s.getId();
        dbHandler.runAction(sql);
        updateLocal();
    }

    public void markAsComplete(String username, int srID){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE ServiceRequest SET status = Complete, completedBy= '" + username + "', completed = '" + time + "' WHERE id = " + srID;
        dbHandler.runAction(sql);
        updateLocal();
    }

    public void updateLocal(){
        String sql = "SELECT * FROM ServiceRequest";
        ResultSet rs = dbHandler.runQuery(sql);
        syncLocalFromDB("ServiceRequest", rs);
    }

    public void updateCompletedBy(ServiceRequests s) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE ServiceRequest SET completedBy = '" + s.getCompletedBy() + "', completed = '" + time + "' WHERE id = " + s.getId();
        dbHandler.runAction(sql);
        updateLocal();
    }

    public void updateAssignedTo(ServiceRequests s) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String sql = "UPDATE ServiceRequest SET started = '" + time + "' WHERE id = " + s.getId();
        dbHandler.runAction(sql);
        updateLocal();
    }

    public void sendServiceRequest(ServiceRequests s) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String sql = "INSERT INTO ServiceRequest(id, type, firstName, lastName, location, instructions, priority, status, createdOn)"
                + " VALUES (" + s.getId()
                + ", '" + s.getType()
                + "', '" + s.getFirstName()
                + "', '" + s.getLastName()
                + "', '" + s.getLocation()
                + "', '" + s.getDescription()
                + "', " + s.getPriority()
                + ", '" + s.getStatus()
                + "', '" + time + "')";
        dbHandler.runAction(sql);
        updateLocal();
    }

    public ResultSet getRequests() {
        String sql = "SELECT * FROM ServiceRequest";
        return dbHandler.runQuery(sql);
    }

    public ResultSet getRequestsOfType(String type) {
        String sql = "SELECT * FROM ServiceRequest WHERE type = '" + type + "'";
        return dbHandler.runQuery(sql);
    }

    public ResultSet getRequestsOfStatus(String status) {
        String sql = "SELECT * FROM ServiceRequest WHERE status = '" + status + "'";
        return dbHandler.runQuery(sql);
    }

    public ResultSet getRequestsOfPriority(int p) {
        String sql = "SELECT * FROM ServiceRequest WHERE priority = " + p;
        return dbHandler.runQuery(sql);
    }


    @Override
    public void initDatabase(DatabaseHandler dbHandler) {
        dbHandler.runSQLScript("init_sr_db.sql");
        dbHandler.runSQLScript("init_sr_li_db.sql");
        dbHandler.runSQLScript("init_sr_rs_db.sql");
        dbHandler.runSQLScript("init_sr_sr_db.sql");
        dbHandler.runSQLScript("init_sr_inbox_db.sql");
        if (dbHandler != DatabaseSingleton.getInstance().getDbHandler()) {
            initDatabase(DatabaseSingleton.getInstance().getDbHandler());
        }
    }

    @Override
    public LinkedList<String> getTableNames() {
        return new LinkedList<>(Collections.singletonList("ServiceRequest"));
    }

    @Override
    public void syncLocalFromDB(String tableName, ResultSet resultSet) {
        try {
            if (tableName.equals("ServiceRequest")) {
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String type = resultSet.getString(2);
                    String firstName = resultSet.getString(3);
                    String lastName = resultSet.getString(4);
                    String location = resultSet.getString(5);
                    String instructions = resultSet.getString(6);
                    int priority = resultSet.getInt(7);
                    String status = resultSet.getString(8);
                    String completedBy = null;
                    Timestamp createdOn = null;
                    Timestamp started = null;
                    Timestamp completed = null;
                    String destNodeID = null;
                    String sourceNodeID = null;
                    try {
                        createdOn = resultSet.getTimestamp(10);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        started = resultSet.getTimestamp(11);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        completed = resultSet.getTimestamp(12);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        destNodeID = resultSet.getString(13);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        sourceNodeID = resultSet.getString(14);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (status.equals("Complete")) {
                        try {
                            completedBy = resultSet.getString(9);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            completedBy = null;
                        }
                    }


                    ServiceRequests s;
                    String[] parts;
                    String special = "";
                    String description = "";
                    if (!type.equals("Security Request")) {
                        parts = instructions.split("/////", 2);
                        special = parts[0];
                        description = parts[1];
                    }

                    switch (type) {
                        case "Religious Services":
                            s = new ReligiousServices(id, firstName, lastName, location, description, status, priority, special, completedBy, createdOn, started, completed);
                            break;

                        case "Language Interpreter":
                            s = new LanguageInterpreter(id, firstName, lastName, location, description, status, priority, special, completedBy, createdOn, started, completed);
                            break;

                        case "Security Request":
                            s = new SecurityRequests(id, location, description, status, priority, completedBy, createdOn, started, completed);
                            break;

                        default:
                            s = new SecurityRequests(id, location, description, status, priority);
                            break;
                    }

                    this.addServiceRequest(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void syncCSVFromDB(DatabaseHandler dbHandler) {
        // intentionally left empty
    }

    public ArrayList<ServiceRequests> getListOfRequests() {
        return listOfRequests;
    }

    private void setListOfRequests(ArrayList<ServiceRequests> listOfRequests) {
        this.listOfRequests = listOfRequests;
    }

    public void addServiceRequest(ServiceRequests s) {
        int listSize = this.listOfRequests.size();
        int flag = 0;
        ArrayList<ServiceRequests> newList = new ArrayList<>();
        if (listSize == 0) {
            newList.add(0, s);
        } else {
            for (ServiceRequests i : this.listOfRequests) {
                if (i.getId() > s.getId() && flag == 0) {
                    newList.add(s);
                    flag++;
                } else {
                    newList.add(i);
                }
            }
            if (flag == 0) {
                newList.add(s);
            }
        }

        this.setListOfRequests(newList);
    }

    public ArrayList<ServiceRequests> addServiceRequest(ServiceRequests s, ArrayList<ServiceRequests> listReq) {
        int listSize = listReq.size();
        int flag = 0;
        ArrayList<ServiceRequests> newList = new ArrayList<>();
        if (listSize == 0) {
            newList.add(0, s);
        } else {
            for (ServiceRequests i : listReq) {
                if (i.getId() > s.getId() && flag == 0) {
                    newList.add(s);
                    flag++;
                } else {
                    newList.add(i);
                }
            }
            if (flag == 0) {
                newList.add(s);
            }
        }

        return newList;
    }

    public ArrayList<ServiceRequests> resultSetToServiceRequest(ResultSet resultSet) {
        ArrayList<ServiceRequests> requests = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String type = resultSet.getString(2);
                String firstName = resultSet.getString(3);
                String lastName = resultSet.getString(4);
                String location = resultSet.getString(5);
                String instructions = resultSet.getString(6);
                int priority = resultSet.getInt(7);
                String status = resultSet.getString(8);
                String completedBy = null;
                Timestamp createdOn = null;
                Timestamp started = null;
                Timestamp completed = null;
                String destNodeID = null;
                String sourceNodeID = null;
                try {
                    createdOn = resultSet.getTimestamp(10);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    started = resultSet.getTimestamp(11);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    completed = resultSet.getTimestamp(12);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    destNodeID = resultSet.getString(13);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    sourceNodeID = resultSet.getString(14);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (status.equals("Complete")) {
                    try {
                        completedBy = resultSet.getString(9);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        completedBy = null;
                    }
                }


                ServiceRequests s;
                String[] parts;
                String special = "";
                String description = "";
                if (!type.equals("Security Request")) {
                    parts = instructions.split("/////", 2);
                    special = parts[0];
                    description = parts[1];
                }

                switch (type) {
                    case "Religious Services":
                        s = new ReligiousServices(id, firstName, lastName, location, description, status, priority, special, completedBy, createdOn, started, completed);
                        break;

                    case "Language Interpreter":
                        s = new LanguageInterpreter(id, firstName, lastName, location, description, status, priority, special, completedBy, createdOn, started, completed);
                        break;

                    case "Security Request":
                        s = new SecurityRequests(id, location, description, status, priority, completedBy, createdOn, started, completed);
                        break;

                    default:
                        s = new SecurityRequests(id, location, description, status, priority);
                        break;
                }

                requests = addServiceRequest(s, requests);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public ServiceRequests getPopUpRequest() {
        return popUpRequest;
    }

    public void setPopUpRequest(ServiceRequests popUpRequest) {
        this.popUpRequest = popUpRequest;
    }

    public String getLastFilter() {
        return lastFilter;
    }

    public void setSearch(String lastFilter, String lastSearch) {
        this.lastFilter = lastFilter;
        this.lastSearch = lastSearch;
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void setSearchNull() {
        this.lastFilter = null;
        this.lastSearch = null;
    }

    public void addUsernameLanguageInterpreter(String username) {
        String sql = "INSERT INTO LanguageInterpreter VALUES ('" + username + "')";
        dbHandler.runAction(sql);
    }

    public void addUsernameReligiousServices(String username) {
        String sql = "INSERT INTO ReligiousServices VALUES ('" + username + "')";
        dbHandler.runAction(sql);
    }

    public void addUsernameSecurityRequest(String username) {
        String sql = "INSERT INTO SecurityRequest VALUES ('" + username + "')";
        dbHandler.runAction(sql);
    }

    public void removeUsernameLanguageInterpreter(String username) {
        String sql = "DELETE FROM LanguageInterpreter WHERE username = '" + username + "'";
        dbHandler.runAction(sql);
    }

    public void removeUsernameReligiousServices(String username) {
        String sql = "DELETE FROM ReligiousServices WHERE username = '" + username + "'";
        dbHandler.runAction(sql);
    }

    public void removeUsernameSecurityRequest(String username) {
        String sql = "DELETE FROM SecurityRequest WHERE username = '" + username + "'";
        dbHandler.runAction(sql);
    }


    public boolean isInTable(String username, String table) {
        ResultSet rs;
        String sql;
        switch (table) {
            case "LanguageInterpreter":
                sql = "SELECT * FROM LanguageInterpreter WHERE username = '" + username + "'";
                break;
            case "ReligiousServices":
                sql = "SELECT * FROM ReligiousServices WHERE username = '" + username + "'";
                break;
            case "SecurityRequest":
                sql = "SELECT * FROM SecurityRequest WHERE username = '" + username + "'";
                break;
            default:
                sql = "SELECT * FROM '" + table + "' WHERE username = '" + username + "'";
                break;
        }
        try {
            rs = dbHandler.runQuery(sql);

            if (!rs.next()) {
                rs.close();
                return false;
            } else {
                rs.close();
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void assignTo(String username, ServiceRequests sr) {
        if (UserSingleton.getInstance().isValidUsername(username)) {
            sr.setStatus("In Progress");
            updateAssignedTo(sr);
            updateStatus(sr);
            String sql = "INSERT INTO Inbox VALUES ('" + username + "', " + sr.getId() + ")";
            DatabaseSingleton.getInstance().getDbHandler().runAction(sql);
        }
        updateLocal();
    }


    public void setGridPaneDimensions(int windowWidth, int windowLength) {
        this.prefWidth = windowWidth;
        this.prefLength = windowLength;
    }

    public int getPrefWidth() {
        return prefWidth;
    }

    public int getPrefLength() {
        return prefLength;
    }

    public void setDestinationLocation(String destNodeID) {
        this.destNodeID = destNodeID;
    }

    public String getDestNodeID() {
        return destNodeID;
    }


    public ArrayList<ServiceRequests> getServiceRequests() {
        ArrayList<ServiceRequests> list = new ArrayList<>();
        String sql = "SELECT * FROM ServiceRequest";
        ResultSet resultSet = dbHandler.runQuery(sql);
        list = resultSetToServiceRequest(resultSet);
        return list;
    }

    public ArrayList<ServiceRequests> getInbox(String username){
        ArrayList<ServiceRequests> inbox;
        String sql = "SELECT S.* FROM Inbox I INNER JOIN ServiceRequest S ON I.requestID = S.id WHERE I.username = '" + username + "'";
        ResultSet resultSet = dbHandler.runQuery(sql);
        inbox = resultSetToServiceRequest(resultSet);
        return inbox;
    }
}
