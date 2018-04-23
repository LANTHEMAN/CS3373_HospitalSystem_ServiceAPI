package edu.wpi.cs3733d18.teamF.api.sr;

import java.sql.Timestamp;

public class SecurityRequests extends ServiceRequests {

    public SecurityRequests(String location, String description, String status, int priority, String staffNeeded) {
        super("Security Request", " ", " ", location, description, status, priority, staffNeeded);
    }

    public SecurityRequests(int id, String location, String description, String status, int priority, String staffNeeded) {
        super("Security Request", id, " ", " ", location, description, status, priority, staffNeeded);
    }

    public SecurityRequests(int id, String location, String description, String status, int priority, String completedBy, Timestamp createdOn, Timestamp started, Timestamp completedOn, String staffNeeded) {
        super("Security Request", id, " ", " ", location, description, status, priority, completedBy, createdOn, started, completedOn, staffNeeded);
    }

}
