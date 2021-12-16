package org.example.issuetracker.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

@Data
@Entity(name = "TACHE")
public class Issue {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
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
