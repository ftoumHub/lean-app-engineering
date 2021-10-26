package org.example.issuetracker.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.issuetracker.model.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ftoum on 19/09/2017.
 */
public class IssuesDto {

    @JsonProperty("_metadata")
    private IssuesMetadata metadata;
    private List<Issue> records;

    public IssuesDto() {
        this.records = new ArrayList<>();
        this.metadata = new IssuesMetadata();
    }

    public IssuesMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(IssuesMetadata metadata) {
        this.metadata = metadata;
    }

    public List<Issue> getRecords() {
        return records;
    }

    public void setRecords(List<Issue> records) {
        this.records = records;
    }
}
