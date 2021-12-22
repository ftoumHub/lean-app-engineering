package org.example.issuetracker.web.api;

import org.example.issuetracker.configuration.exception.ResourceNotFoundException;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.service.IssueService;
import org.example.issuetracker.web.dto.IssuesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api")
public class IssueController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * Retourne la liste des issues
     */
    @GetMapping(value = "/issues", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<IssuesDto> getAllIssues(@RequestParam(required = false, value = "status") final String status,
                                                  @RequestParam(required = false, value = "effort_gte") final Integer effortGte,
                                                  @RequestParam(required = false, value = "effort_lte") final Integer effortLte) {
        IssuesDto issuesDto = new IssuesDto();
        try {
            List<Issue> issues = issueService.findAll();

            if (status != null) {
                logger.info("> getAllIssues with status : " + status);
                issues = issues.stream().filter(getIssueWithStatus(status)).collect(toList());
            }
            if (effortLte != null) {
                logger.info("> getAllIssues with effort lower than : " + effortLte);
                issues = issues.stream().filter(getIssueWithEffortLowerThan(effortLte)).collect(toList());
            }
            if (effortGte != null) {
                logger.info("> getAllIssues with effort greater than : " + effortGte);
                issues = issues.stream().filter(getIssueWithEffortGreaterThan(effortGte)).collect(toList());
            }

            issuesDto.setRecords(issues);
            issuesDto.getMetadata().setTotalCount(issues.size());
        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(issuesDto, INTERNAL_SERVER_ERROR);
        }

        logger.info("< getAllIssues");
        return new ResponseEntity<>(issuesDto, OK);
    }

    private Predicate<Issue> getIssueWithStatus(String status) {
        return issue -> issue.getStatus().equals(IssueStatus.fromStatus(status));
    }

    private Predicate<Issue> getIssueWithEffortLowerThan(Integer effortLte) {
        return issue -> issue.getEffort() < effortLte;
    }

    private Predicate<Issue> getIssueWithEffortGreaterThan(Integer effortGte) {
        return issue -> issue.getEffort() > effortGte;
    }

    /**
     * Récupère une question grâce à son id
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/issues/{id}")
    public ResponseEntity<?> getIssue(@PathVariable("id") UUID id) {
        logger.info("> getIssue with id : " + id);

        Optional<Issue> issue;

        try {

            verifyIssue(id);
            issue = issueService.find(id);

        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(e, NOT_FOUND);
        }

        logger.info("< getIssue");
        return new ResponseEntity<>(issue, OK);
    }


    /**
     * Ajoute une question.
     *
     * @param issue
     * @return
     */
    @PostMapping(value = "/issues", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue) {
        logger.info("> createIssue");

        Issue createdIssue;
        try {
            createdIssue = issueService.create(issue);
        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }

        logger.info("< createIssue");
        return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
    }

    @PutMapping(value = "/issues/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Issue> updateIssue(@RequestBody Issue issue) {
        logger.info("> updateIssue");

        Issue updatedIssue = null;
        try {
            verifyIssue(issue.getId());
            updatedIssue = issueService.update(issue);
        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }

        logger.info("< updateIssue");
        return new ResponseEntity<>(updatedIssue, OK);
    }

    @DeleteMapping(value = "/issues/{id}")
    public ResponseEntity<Issue> deleteIssue(@PathVariable("id") UUID issueId) {
        logger.info("> deleteIssue");

        try {
            verifyIssue(issueId);
            issueService.delete(issueId);
        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }

        logger.info("< deleteIssue");
        return new ResponseEntity<>(NO_CONTENT);
    }

    protected void verifyIssue(UUID issueId) throws ResourceNotFoundException {
        //if (!issueId.toString().matches("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$")) {
        if(!issueId.toString().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")){
            throw new ResponseStatusException(BAD_REQUEST, "This id " + issueId + " isn't an UUID");
        } else {
            Optional<Issue> issue = issueService.find(issueId);

            // if no issue found, return 404 status code
            if (!issue.isPresent()) {
                throw new ResourceNotFoundException("Issue with id " + issueId + " not found");
            }
        }

    }
}
