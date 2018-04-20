package edu.wpi.cs3733d18.teamF.api.sr;

import java.sql.Timestamp;

public class SecurityRequest extends ServiceRequest {

    public SecurityRequest(String location, String description, String status, int priority) {
        super("Security Request", " ", " ", location, description, status, priority);
    }

    public SecurityRequest(int id, String location, String description, String status, int priority) {
        super("Security Request", id, " ", " ", location, description, status, priority);
    }

    public SecurityRequest(int id,String location, String description, String status, int priority, String assignedTo) {
        super("Security Request", id, " ", " ", location, description, status, priority, assignedTo);

    }

    public SecurityRequest(int id, String location, String description, String status, int priority, String assignedTo, String completedBy, Timestamp createdOn, Timestamp started, Timestamp completedOn) {
        super("Security Request", id, " ", " ", location, description, status, priority, assignedTo, completedBy, createdOn, started, completedOn);
    }

}
