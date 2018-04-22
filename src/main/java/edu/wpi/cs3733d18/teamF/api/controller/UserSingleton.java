package edu.wpi.cs3733d18.teamF.api.controller;

import edu.wpi.cs3733d18.teamF.api.db.DatabaseHandler;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseSingleton;
import edu.wpi.cs3733d18.teamF.api.sr.ServiceRequestSingleton;
import edu.wpi.cs3733d18.teamF.api.voice.VoiceLauncher;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSingleton {
    DatabaseHandler dbHandler;
    private String userPrivilege;
    private String currUser;

    private UserSingleton() {
        this.dbHandler = DatabaseSingleton.getInstance().getDbHandler();
        userPrivilege = Privilege.STAFF;
        currUser = "staff";

    }

    public static UserSingleton getInstance() {
        return loginHelper.INSTANCE;
    }

    private static String getPrivilege(String type) {
        if (type.equals("Admin")) {
            return Privilege.ADMIN;
        } else {
            return Privilege.STAFF;
        }
    }

    public boolean isAdmin() {
        return (userPrivilege.equals(Privilege.ADMIN));
    }


    public String getCurrUser() {
        return currUser;
    }


    public void setCurrUser(String username, String privilege){
        this.currUser = username;
        this.userPrivilege = privilege;
    }

    public void addUser(User u) {
        String sql = "INSERT INTO HUser"
                + " VALUES ('" + u.getUname()
                + "', '" + u.getFirstName()
                + "', '" + u.getLastName()
                + "', '" + u.getOccupation()
                + "')";
        dbHandler.runAction(sql);
    }

    public void removeUser(User u) {
        String sql = "DELETE FROM HUser WHERE username = '" + u.getUname() + "'";
        dbHandler.runAction(sql);
        sql = "SELECT * FROM HUser";
        ResultSet resultSet = UserSingleton.getInstance().dbHandler.runQuery(sql);
    }

    public void updateUser(User u) {
        String sql = "UPDATE HUser SET firstName = '" + u.getFirstName()
                + "', lastName = '" + u.getLastName()
                + "', occupation = '" + u.getOccupation()
                + "' WHERE username = '" + u.getUname() + "'";
        dbHandler.runAction(sql);
        sql = "SELECT * FROM HUser";
        ResultSet resultSet = UserSingleton.getInstance().dbHandler.runQuery(sql);
    }

    public boolean userExist(String username) {
        ResultSet rs;
        String sql = "SELECT * FROM HUser WHERE username = '" + username + "'";
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

    public String getUserPrivilege() {
        return userPrivilege;
    }

    private static class loginHelper {
        static final UserSingleton INSTANCE = new UserSingleton();
    }

    public static class Privilege {
        public static final String ADMIN = "Admin";
        public static final String STAFF = "Staff";
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    @Override
    public void finalize() {
        VoiceLauncher.getInstance().terminate();
    }

}

