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

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = "/api")
public class IssueController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * Retourne la liste des issues
     */
    @RequestMapping(value = "/issues", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
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
        return issue -> issue.getStatus().equals(IssueStatus.valueOf(status.toUpperCase()));
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
    @RequestMapping(value = "/issues/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getIssue(@PathVariable("id") Long id) {
        logger.info("> getIssue with id : " + id);

        Issue issue = null;

        try {
            verifyIssue(id);
            issue = issueService.find(id);
        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(issue, INTERNAL_SERVER_ERROR);
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
    @RequestMapping(value = "/issues", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue) {
        logger.info("> createIssue");

        Issue createdIssue = null;
        try {
            createdIssue = issueService.create(issue);
        } catch (Exception e) {
            logger.error("Unexpected Exception caught.", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }

        logger.info("< createIssue");
        return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/issues/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/issues/{id}", method = DELETE)
    public ResponseEntity<Issue> deleteIssue(@PathVariable("id") Long issueId) {
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

    protected void verifyIssue(Long issueId) throws ResourceNotFoundException {
        Issue issue = issueService.find(issueId);
        // if no issue found, return 404 status code
        if(issue == null) {
            throw new ResourceNotFoundException("Issue with id " + issueId + " not found");
        }
    }

}
