package edu.wpi.cs3733d18.teamF.api.sr;

import java.sql.Timestamp;

public class LanguageInterpreter extends ServiceRequest {
    private String language;

    public LanguageInterpreter(String firstName, String lastName, String location, String description, String status, int priority, String language) {
        super("Language Interpreter", firstName, lastName, location, description, status, priority);
        this.language = language;
    }

    public LanguageInterpreter(int id, String firstName, String lastName, String location, String description, String status, int priority, String language) {
        super("Language Interpreter", id, firstName, lastName, location, description, status, priority);
        this.language = language;
    }

    public LanguageInterpreter(int id, String firstName, String lastName, String location, String description, String status, int priority, String language, String assignedTo) {
        super("Language Interpreter", id, firstName, lastName, location, description, status, priority, assignedTo);
        this.language = language;
    }

    public LanguageInterpreter(int id, String firstName, String lastName, String location, String description, String status, int priority, String language, String assignedTo, String completedBy, Timestamp createdOn, Timestamp started, Timestamp completedOn) {
        super("Language Interpreter", id, firstName, lastName, location, description, status, priority, assignedTo, completedBy, createdOn, started, completedOn);
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void parseIntoDescription(){
        super.setDescription(getLanguage() + "\n" + getDescription());
    }
}
