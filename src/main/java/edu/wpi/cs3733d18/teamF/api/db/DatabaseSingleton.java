package edu.wpi.cs3733d18.teamF.api.db;

public class DatabaseSingleton {
    private DatabaseHandler dbHandler;

    private DatabaseSingleton() {
        dbHandler = new DatabaseHandler();
    }

    public static DatabaseSingleton getInstance() {
        return LazyInitializer.INSTANCE;
    }

    public DatabaseHandler getDbHandler() {
        return dbHandler;
    }

    private static class LazyInitializer {
        static final DatabaseSingleton INSTANCE = new DatabaseSingleton();
    }
}
