package edu.wpi.cs3733d18.teamF.api.db;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashSet;

public class DatabaseHandler {
    private String databaseURL = "jdbc:derby:database_teamF_SR_API;create=true";
    private Connection connection = null;
    private HashSet<DatabaseItem> trackedItems = new HashSet<>();

    public DatabaseHandler() {
        connectToDatabase();
    }

    public DatabaseHandler(String directoryName) {
        databaseURL = "jdbc:derby:" + directoryName + ";create=true";
        connectToDatabase();
    }

    public void disconnectFromDatabase() {
        try {
            for(DatabaseItem trackedItem : trackedItems){
                trackedItem.syncCSVFromDB(this);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void trackAndInitItem(DatabaseItem dbItem) {
        trackedItems.add(dbItem);
        dbItem.initDatabase(this);
        syncLocalFromDB(dbItem);
        dbItem.syncCSVFromDB(this);
    }

    public void syncLocalFromDB(DatabaseItem dbItem) {
        for (String table : dbItem.getTableNames()) {
            ResultSet rs = runQuery("SELECT * FROM " + table);
            dbItem.syncLocalFromDB(table, rs);
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncCSVFromDB(DatabaseItem dbItem) {
        dbItem.syncCSVFromDB(this);
    }

    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            if (rs.getString(3).equals(tableName)) {
                rs.close();
                return true;
            }
        }
        rs.close();
        return false;
    }

    public void runSQLScript(String script) {
        try {
            org.apache.derby.tools.ij.runScript(connection, getClass().getResourceAsStream(script)
                    , StandardCharsets.UTF_8.name()
                    , new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                        }
                    }, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runVerboseSQLScript(String script) {
        try {
            org.apache.derby.tools.ij.runScript(connection, getClass().getResourceAsStream(script)
                    , StandardCharsets.UTF_8.name()
                    , System.out, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet runQuery(String query) {
        ResultSet result;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(query);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void runAction(String action) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(databaseURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
