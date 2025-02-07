package com.example.n099madassignment2;

public class Task {
    private String id;
    private String description;
    private String priority;

    public Task() {}

    public Task(String id, String description, String priority) {
        this.id = id;
        this.description = description;
        this.priority = priority;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
