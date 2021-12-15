package org.example.issuetracker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

import com.github.javafaker.Faker;
import org.example.issuetracker.model.Issue;
import org.example.issuetracker.model.IssueStatus;
import org.example.issuetracker.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    ConfigurableApplicationContext context;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    public void run(String... args) {
        IssueRepository repository = context.getBean(IssueRepository.class);
        repository.deleteAll();
        log.info("> Inserting new data...");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Faker faker = new Faker();

            for (int i = 0; i < 100; i++) {

                Issue fakeIssue = new Issue();
                fakeIssue.setTitle(faker.lorem().sentence(10));
                fakeIssue.setEffort(faker.number().numberBetween(1, 8));
                fakeIssue.setStatus(IssueStatus.values()[new Random().nextInt(IssueStatus.values().length)]);
                fakeIssue.setOwner(faker.name().fullName());
                fakeIssue.setCreated(faker.date().birthday());

                repository.save(fakeIssue);
            }
            Issue issue1 = new Issue();
            issue1.setStatus(IssueStatus.IN_PROGRESS);
            issue1.setOwner("Guillaume");
            issue1.setCreated(df.parse("2017-09-29"));
            issue1.setEffort(1);
            issue1.setTitle("Tester le générateur d'appli FUN");
            repository.save(issue1);

            Issue issue2 = new Issue();
            issue2.setStatus(IssueStatus.OPEN);
            issue2.setOwner("Guillaume");
            issue2.setCreated(df.parse("2017-10-02"));
            issue2.setEffort(1);
            issue2.setCompletionDate(df.parse("2017-10-03"));
            issue2.setTitle("Importer le projet Admin REFCG dans SVN");
            repository.save(issue2);

            Issue issue3 = new Issue();
            issue3.setStatus(IssueStatus.ASSIGNED);
            issue3.setOwner("Georges");
            issue3.setCreated(df.parse("2017-10-02"));
            issue3.setEffort(5);
            issue3.setCompletionDate(df.parse("2017-10-06"));
            issue3.setTitle("Réaliser une maquette de l'appli en React");
            repository.save(issue3);

            Issue issue4 = new Issue();
            issue4.setStatus(IssueStatus.ASSIGNED);
            issue4.setOwner("Georges");
            issue4.setCreated(df.parse("2017-10-03"));
            issue4.setEffort(3);
            issue4.setCompletionDate(df.parse("2017-10-06"));
            issue4.setTitle("Récupérer le generator-funapp à jour");
            repository.save(issue4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}