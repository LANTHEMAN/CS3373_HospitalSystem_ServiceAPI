package edu.wpi.cs3733d18.teamF.sr;

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

    public LanguageInterpreter(int id, String firstName, String lastName, String location, String description, String status, int priority, String language, String assignedTo, String completedBy) {
        super("Language Interpreter", id, firstName, lastName, location, description, status, priority, assignedTo, completedBy);
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
