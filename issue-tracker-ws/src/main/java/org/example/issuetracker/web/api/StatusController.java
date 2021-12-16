package org.example.issuetracker.web.api;

import org.example.issuetracker.model.IssueStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class StatusController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public IssueStatus[] getAllStatus() {
        return IssueStatus.values();
    }
}