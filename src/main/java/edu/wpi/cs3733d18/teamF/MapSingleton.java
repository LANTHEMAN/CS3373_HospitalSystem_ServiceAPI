package edu.wpi.cs3733d18.teamF;

import edu.wpi.cs3733d18.teamF.db.DatabaseHandler;
import edu.wpi.cs3733d18.teamF.db.DatabaseSingleton;

public class MapSingleton {

    DatabaseHandler dbHandler;
    Map map;

    private MapSingleton() {
        this.dbHandler = DatabaseSingleton.getInstance().getDbHandler();
        this.map = new Map();
        dbHandler.trackAndInitItem(map);
    }

    private static class LazyInitializer {
        static final MapSingleton INSTANCE = new MapSingleton();
    }

    public static MapSingleton getInstance() {
        return LazyInitializer.INSTANCE;
    }

    public Map getMap(){
        return map;
    }

}

