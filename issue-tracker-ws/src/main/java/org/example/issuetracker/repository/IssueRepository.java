package org.example.issuetracker.repository;

import org.example.issuetracker.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class IssueRepository  {

    public void deleteAll() {
        System.out.println("");
    }

    public Issue save(Issue issue) {
        return null;
    }
}
