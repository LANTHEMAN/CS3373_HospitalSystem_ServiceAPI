package edu.wpi.cs3733d18.teamF.api.sr;

import java.sql.Timestamp;

public class ReligiousServices extends ServiceRequests {
    private String religion;

    public ReligiousServices(String firstName, String lastName, String location, String description, String status, int priority, String religion, String staffNeeded) {
        super("Religious Services", firstName, lastName, location, description, status, priority, staffNeeded);
        this.religion = religion;
    }

    public ReligiousServices(int id, String firstName, String lastName, String location, String description, String status, int priority, String religion, String staffNeeded) {
        super("Religious Services", id, firstName, lastName, location, description, status, priority, staffNeeded);
        this.religion = religion;
    }


    public ReligiousServices(int id, String firstName, String lastName, String location, String description, String status, int priority, String religion, String completeBy, Timestamp createdOn, Timestamp started, Timestamp completedOn, String staffNeeded) {
        super("Religious Services", id, firstName, lastName, location, description, status, priority, completeBy, createdOn, started, completedOn, staffNeeded);
        this.religion = religion;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

}
