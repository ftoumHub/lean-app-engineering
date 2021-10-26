package org.example.issuetracker.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity(name = "TACHE")
public class Issue {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String owner;
    private Date created;
    private Integer effort;
    private Date completionDate;
    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    public Issue() {
    }
}
