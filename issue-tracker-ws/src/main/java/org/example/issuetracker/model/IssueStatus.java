package org.example.issuetracker.model;

public enum IssueStatus {
    NEW("New"),
    OPEN("Open"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("in Progress"),
    DONE("Done");

    private final String status;

    IssueStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
