package org.example.issuetracker.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IssueService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IssueRepository issueRepository;
    private MeterRegistry counterService;

    public IssueService(IssueRepository issueRepository,
                        MeterRegistry counterService) {
        this.issueRepository = issueRepository;
        this.counterService = counterService;
    }

    /**
     * Search the issue data repository for all Issue entities.
     * @return A List of Issue entities or null if none found.
     */
    public List<Issue> findAll() {
        logger.info("> findAll");

        counterService.counter("services.issueservice.findAll.invoked");


        List<Issue> issues = issueRepository.findAll();

        logger.info("< findAll");
        return issues;
    }

    /**
     * Search the issue data repository for a single Issue entity by the primary
     * key identifier.
     * @param id An issue primary key identifier.
     * @return An Issue entity or null if not found.
     */
    public Optional<Issue> find(Long id) {
        logger.info("> find id:{}", id);

        counterService.counter("services.issueservice.find.invoked");

        Optional<Issue> issue = issueRepository.findById(id);

        logger.info("< find id:{}", id);
        return issue;
    }

    /**
     * Create a new Issue entity in the data repository.
     * @param issue An issue entity to persist.
     * @return The persisted issue entity.
     */
    public Issue create(Issue issue) {
        logger.info("> create");

        counterService.counter("services.issueservice.create.invoked");

        Issue persistedIssue = issueRepository.save(issue);

        logger.info("< create");
        return persistedIssue;
    }

    /**
     * Update an Issue entity in the data repository.
     * @param issue An issue entity to update.
     * @return The updated issue entity.
     */
    public Issue update(Issue issue) {
        logger.info("> update");

        counterService.counter("services.issueservice.update.invoked");

        Issue updatedIssue = issueRepository.save(issue);

        logger.info("< update");
        return updatedIssue;
    }

    /**
     * Delete an Issue entity from the data repository.
     * @param id The primary key identifier of the issue to delete.
     */
    public void delete(Long id) {
        logger.info("> delete");

        counterService.counter("services.issueservice.delete.invoked");

        issueRepository.deleteById(id);

        logger.info("< delete");
    }

}
