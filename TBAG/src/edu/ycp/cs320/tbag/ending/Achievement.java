package edu.ycp.cs320.tbag.ending;

public class Achievement {
    private String id;
    private String description;
    private boolean completed;

    public Achievement(String id, String description) {
        this.id = id;
        this.description = description;
        this.completed = false;
    }
    
    public Achievement(String id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }
}
