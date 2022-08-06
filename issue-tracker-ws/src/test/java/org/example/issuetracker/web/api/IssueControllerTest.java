package org.example.issuetracker.web.api;

import org.example.issuetracker.configuration.exception.ResourceNotFoundException;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.repository.IssueRepository;
import org.example.issuetracker.web.dto.error.ErrorDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.issuetracker.model.IssueStatus.NEW;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
public class IssueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IssueRepository issueRepository;

    // This object will be initialized thanks to @AutoConfigureJsonTesters
    @Autowired
    private JacksonTester<Issue> jsonIssue;

    @Autowired
    private JacksonTester<ErrorDetail> jsonError;
    @Test
    public void canRetrieveByUuidWhenExists() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final Issue issue = new Issue(uuid, "une nouvelle tâche", "owner", null, 1, null, NEW);
        // given
        given(issueRepository.findById(uuid)).willReturn(Optional.of(issue));

        // when
        MockHttpServletResponse response = mvc.perform(
                get(format("/api/issues/%s", uuid))
                        .accept(APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(OK.value());
        assertThat(response.getContentAsString(UTF_8))
                .isEqualTo(jsonIssue.write(issue).getJson());
    }

    @Test
    public void failsWhenStringPassed() throws Exception {
        // when
        MockHttpServletResponse response = mvc.perform(
                get(format("/api/issues/1")).accept(APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.value());
        // then
        //final String errorAsString = response.getContentAsString();
    }

    @Test
    public void throwsResourceNotFoundExceptionWhenDoesNotExist() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final ErrorDetail error = new ErrorDetail("Resource Not Found", 404, format("Issue with id %s not found", uuid), 1L, ResourceNotFoundException.class.getName(), null);
        // given
        given(issueRepository.findById(uuid))
                .willThrow(new ResourceNotFoundException(format("Issue with id %s not found", uuid)));

        // when
        MockHttpServletResponse response = mvc.perform(
                get(format("/api/issues/%s", uuid)).accept(APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.value());

        final String errorAsString = response.getContentAsString();
        final ErrorDetail errorDetail = jsonError.parse(errorAsString).getObject();

        assertThat(errorAsString)
                .isEqualTo(jsonError.write(error.toBuilder().timeStamp(errorDetail.getTimeStamp()).build()).getJson());
    }

    @Test
    public void canCreateANewIssue() throws Exception {
        final Issue issue = new Issue(UUID.randomUUID(), "title", "owner", null, 1, null, NEW);
        // when
        MockHttpServletResponse response = mvc.perform(
                post("/api/issues/").contentType(APPLICATION_JSON).content(
                        jsonIssue.write(issue).getJson()
                )).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(CREATED.value());
    }
}
