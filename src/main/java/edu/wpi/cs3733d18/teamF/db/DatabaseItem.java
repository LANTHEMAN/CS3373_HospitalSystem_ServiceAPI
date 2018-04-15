package edu.wpi.cs3733d18.teamF.db;

import java.sql.ResultSet;
import java.util.LinkedList;

public interface DatabaseItem {
    // given access to the database, initialize the Table and load it from the csv
    void initDatabase(DatabaseHandler dbHandler);

    // returns the name of the table
    LinkedList<String> getTableNames();

    // given the entire table from the DB, sync locally
    void syncLocalFromDB(String tableName, ResultSet resultSet);

    // given access to the database, sync the local csv file
    void syncCSVFromDB(DatabaseHandler dbHandler);
}
