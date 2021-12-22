package org.example.issuetracker.web.api;

import org.example.issuetracker.configuration.exception.ResourceNotFoundException;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
class IssueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IssueRepository issueRepository;

    // This object will be initialized thanks to @AutoConfigureJsonTesters
    @Autowired
    private JacksonTester<Issue> jsonIssue;

    // ...

    @Test
    void canRetrieveByUuidWhenExists() throws Exception {
        // ...
        // ...
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Issue issue1 = new Issue();
        issue1.setId(UUID.randomUUID());
        issue1.setStatus(IssueStatus.IN_PROGRESS);
        issue1.setOwner("Guillaume");
        issue1.setCreated(df.parse("2017-09-29"));
        issue1.setEffort(1);
        issue1.setTitle("Tester le générateur d'appli FUN");

        issueRepository.save(issue1);
        jsonIssue.write(issue1);

        // given
        given(issueRepository.findById(issue1.getId())).willReturn(Optional.of(issue1));


        // when
        MockHttpServletResponse response = mvc.perform(get(format("/api/issues/%s", issue1.getId())).accept(APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(OK.value());
        //assertThat(response.getContentAsString()).isEqualTo(.getJson());
    }

    @Test
    void throwsResourceNotFoundExceptionWhenDoesNotExist() throws Exception {
        UUID uuid = UUID.randomUUID();

        // given
        given(issueRepository.findById(uuid))
                .willThrow(new ResourceNotFoundException(format("Issue with id %s not found", uuid)));


        // when
        MockHttpServletResponse response = mvc.perform(get(format("/api/issues/%s", uuid)).accept(APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.value());

        //final String errorAsString = response.getContentAsString();
        // ....;
        //assertThat(errorAsString).isEqualTo(.getJson());
    }

    @Test
    void canCreateANewIssue() throws Exception {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Issue issue1 = new Issue();
        issue1.setId(UUID.randomUUID());
        issue1.setStatus(IssueStatus.IN_PROGRESS);
        issue1.setOwner("Guillaume");
        issue1.setCreated(df.parse("2017-09-29"));
        issue1.setEffort(1);
        issue1.setTitle("Tester le générateur d'appli FUN");

        // when
        MockHttpServletResponse response = mvc.perform(
                post("/api/issues/").contentType(APPLICATION_JSON).content(jsonIssue.write(issue1).getJson()
                )).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(CREATED.value());
    }
}