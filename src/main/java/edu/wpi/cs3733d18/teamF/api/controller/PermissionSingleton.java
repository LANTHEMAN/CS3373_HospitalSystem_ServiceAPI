package edu.wpi.cs3733d18.teamF.api.controller;

import edu.wpi.cs3733d18.teamF.api.db.DatabaseHandler;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseSingleton;
import edu.wpi.cs3733d18.teamF.api.sr.ServiceRequestSingleton;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionSingleton {
    DatabaseHandler dbHandler;
    String userPrivilege;
    String currUser;

    private PermissionSingleton() {
        this.dbHandler = DatabaseSingleton.getInstance().getDbHandler();
        userPrivilege = Privilege.GUEST;
        currUser = "admin";

        if (!userExist("admin")) {
            addUser(new User("admin", "Admin", "Default", "Admin"));
            if (!ServiceRequestSingleton.getInstance().isInTable("admin", "LanguageInterpreter")) {
                ServiceRequestSingleton.getInstance().addUsernameLanguageInterpreter("admin");
                ServiceRequestSingleton.getInstance().addUsernameReligiousServices("admin");
                ServiceRequestSingleton.getInstance().addUsernameSecurityRequest("admin");
            }
        }

        if (!userExist("staff")) {
            addUser(new User("staff", "Staff", "Member", "Nurse"));
            if (!ServiceRequestSingleton.getInstance().isInTable("staff", "LanguageInterpreter")) {
                ServiceRequestSingleton.getInstance().addUsernameLanguageInterpreter("staff");
                ServiceRequestSingleton.getInstance().addUsernameReligiousServices("staff");
            }
        }

    }

    public static PermissionSingleton getInstance() {
        return loginHelper.INSTANCE;
    }

    private static String getPrivilege(String type) {
        if (type.equals("Admin")) {
            return Privilege.ADMIN;
        } else if (type.equals("Staff")) {
            return Privilege.STAFF;
        } else {
            return Privilege.GUEST;
        }
    }

    public boolean isAdmin() {
        return (userPrivilege.equals(Privilege.ADMIN));
    }


    public String getCurrUser() {
        return currUser;
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
        ResultSet resultSet = PermissionSingleton.getInstance().dbHandler.runQuery(sql);
    }

    public void updateUser(User u) {
        String sql = "UPDATE HUser SET firstName = '" + u.getFirstName()
                + "', lastName = '" + u.getLastName()
                + "', occupation = '" + u.getOccupation()
                + "' WHERE username = '" + u.getUname() + "'";
        dbHandler.runAction(sql);
        sql = "SELECT * FROM HUser";
        ResultSet resultSet = PermissionSingleton.getInstance().dbHandler.runQuery(sql);
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
        static final PermissionSingleton INSTANCE = new PermissionSingleton();
    }

    public static class Privilege {
        public static final String GUEST = "Guest";
        public static final String ADMIN = "Admin";
        public static final String STAFF = "Staff";
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

}

