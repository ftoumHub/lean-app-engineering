package org.example.issuetracker.model;

public enum IssueStatus {
    NEW("New"),
    OPEN("Open"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String status;
    
    IssueStatus(String status) {
        this.status = status;
    }

    public String toStatus() {
        return status;
    }

}
