package edu.wpi.cs3733d18.teamF.api.controller;

import edu.wpi.cs3733d18.teamF.api.db.DatabaseHandler;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseSingleton;


import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class UserSingleton {
    DatabaseHandler dbHandler;
    private String userPrivilege;
    private String currUser;
    private ArrayList<String> usernames;

    private UserSingleton() {
        this.dbHandler = DatabaseSingleton.getInstance().getDbHandler();
        userPrivilege = Privilege.STAFF;
        currUser = "staff";
        usernames = new ArrayList<>();

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

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    public boolean isValidUsername(String username){
        Predicate<String> p = e -> e == username;
        for(String s:this.usernames){
            if(Objects.equals(username, s)){
                return true;
            }
        }

        return false;
    }
}

