package org.example.issuetracker;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.github.javafaker.Faker;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.example.issuetracker.model.IssueStatus.DONE;


@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final Faker faker = new Faker();

    final IssueRepository repository;

    public Application(IssueRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void run(String... args) {
        repository.deleteAll();
        log.info("> Inserting new data...");

        for (int i = 0; i < 100; i++) {
            final String title = faker.lorem().sentence(10);
            final String owner = faker.name().fullName();
            final Date created = faker.date().past(10, TimeUnit.DAYS);
            final int effort = faker.number().numberBetween(1, 20);
            final IssueStatus status = IssueStatus.values()[new Random().nextInt(IssueStatus.values().length)];
            final Date completionDate = status == DONE ? faker.date().future(20, TimeUnit.DAYS): null;

            repository.save(new Issue(null, title, owner, created, effort, completionDate, status));
        }
    }

}