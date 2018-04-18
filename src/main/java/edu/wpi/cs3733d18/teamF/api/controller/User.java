package edu.wpi.cs3733d18.teamF.api.controller;


public class User {
    String uname;
    String firstName;
    String lastName;
    String occupation;


    public User(String uname, String firstName, String lastName, String occupation) {
        this.uname = uname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }


}
