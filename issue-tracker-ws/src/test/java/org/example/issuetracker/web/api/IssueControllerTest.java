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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
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

    final Issue issue1 = new Issue(UUID.randomUUID(), "Tester le générateur d'appli FUN", "Guillaume", null, 1, null, IssueStatus.NEW);
    // ...

    @Test
    void canRetrieveByUuidWhenExists() throws Exception {

        // given
        given(issueRepository.findById(issue1.getId())).willReturn(Optional.of(issue1));

        // when
        MockHttpServletResponse response = mvc.perform(get(format("/api/issues/%s", issue1.getId())).accept(APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(OK.value());

        assertThat(response.getContentAsString(StandardCharsets.UTF_8)).isEqualTo(jsonIssue.write(issue1).getJson());
    }

    @Test
    void throwsResourceNotFoundExceptionWhenDoesNotExist() throws Exception {

        // given
        given(issueRepository.findById(issue1.getId()))
                .willThrow(new ResourceNotFoundException(format("Issue with id %s not found", issue1.getId())));

        // when
        MockHttpServletResponse response = mvc.perform(get(format("/api/issues/%s", issue1.getId())).accept(APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.value());

        final String errorAsString = response.getContentAsString();
        // ....

        // Je ne vois pas sur quoi il faut récupérer le json alors que ça doit corresponre à une chaine de caractère
        assertThat(errorAsString).isEqualTo("Can't find " + issue1.getId());
    }

    @Test
    void canCreateANewIssue() throws Exception {

        // when
        MockHttpServletResponse response = mvc.perform(
                post("/api/issues/").contentType(APPLICATION_JSON).content(jsonIssue.write(issue1).getJson()
                )).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(CREATED.value());
    }
}