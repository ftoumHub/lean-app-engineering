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

    public static IssueStatus fromStatus(String value) {
        for (IssueStatus stat : IssueStatus.values()) {
            if (stat.status.equals(value))
                return stat;
        }
        throw new IllegalStateException(String.format("Unsupported type %s.", value));
    }

}
