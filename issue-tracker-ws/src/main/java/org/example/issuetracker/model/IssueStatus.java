package org.example.issuetracker.model;

import java.util.Arrays;

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

    public static IssueStatus fromStatus(String status) {
        return Arrays.stream(IssueStatus.values())
                .filter(s -> s.status.equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Le status " + status + " est inconnu dans IssueStatus"));
    }

}
