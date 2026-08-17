package io.github.trieunguyenphu.examflow.config;

import io.github.trieunguyenphu.examflow.model.Exam;
import io.github.trieunguyenphu.examflow.model.Question;
import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.ExamRepository;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("demo")
public class DemoDataInitializer implements CommandLineRunner {

    public static final String ADMIN_EMAIL = "admin@examflow.local";
    public static final String STUDENT_EMAIL = "trieu@examflow.local";
    public static final String DEMO_PASSWORD = "ExamFlowDemo2026";

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private static final List<ExamSeed> EXAM_SEEDS = List.of(
            new ExamSeed("Java Programming Fundamentals", "Core Java, collections, exceptions and the JVM.", 18, List.of(
                    q("What does the Java compiler normally produce from a .java source file?", 2, "Native machine code", "JVM bytecode", "A Docker image", "SQL statements"),
                    q("Which Java collection preserves insertion order and allows duplicate values?", 1, "ArrayList", "HashSet", "TreeSet", "HashMap"),
                    q("What is the main benefit of try-with-resources?", 3, "It retries failed code", "It makes every object immutable", "It closes AutoCloseable resources", "It disables checked exceptions"),
                    q("Which statement about java.lang.String is correct?", 4, "It is mutable", "It cannot be compared", "It stores only ASCII", "It is immutable"),
                    q("What does final on an instance method prevent?", 2, "Calling the method", "Overriding the method", "Overloading the method", "Returning null"),
                    q("What is the average lookup complexity of a well-sized HashMap?", 1, "O(1)", "O(log n)", "O(n)", "O(n²)"))),
            new ExamSeed("Python Programming Essentials", "Python syntax, data structures and runtime conventions.", 18, List.of(
                    q("Which expression creates a list of squares from 0 through 4?", 3, "square(0, 4)", "[x * x where x in 5]", "[x * x for x in range(5)]", "map(x * x, 5)"),
                    q("What does Python's with statement primarily manage?", 2, "Package installation", "Resource context and cleanup", "Type conversion", "Loop iteration"),
                    q("Which built-in Python collection is immutable?", 4, "list", "set", "dict", "tuple"),
                    q("What does the is operator compare?", 1, "Object identity", "String ordering", "Numeric size", "Value equality only"),
                    q("Why use a Python virtual environment?", 3, "To compile Python to Java", "To speed up every loop", "To isolate project dependencies", "To replace source control"),
                    q("When is code inside if __name__ == '__main__' executed?", 2, "Whenever the file is imported", "When the file runs directly", "Only during unit tests", "Only inside a class"))),
            new ExamSeed("DevOps and CI/CD", "Delivery pipelines, infrastructure automation and safe releases.", 20, List.of(
                    q("What is the primary goal of continuous integration?", 1, "Integrate and verify changes frequently", "Deploy every commit to production", "Remove code review", "Avoid automated tests"),
                    q("What distinguishes continuous delivery from continuous deployment?", 4, "Delivery has no pipeline", "Deployment has no tests", "Delivery requires containers", "Delivery keeps production release as an explicit decision"),
                    q("What is a key benefit of infrastructure as code?", 2, "Servers never fail", "Infrastructure changes become repeatable and versioned", "Monitoring is unnecessary", "Every environment becomes public"),
                    q("Which check should decide whether a new instance can receive traffic?", 3, "Liveness probe only", "Disk size check", "Readiness probe", "Source-code line count"),
                    q("How does a blue-green deployment reduce release risk?", 1, "It switches traffic between two complete environments", "It removes the previous version first", "It deploys without monitoring", "It shares one process for both versions"),
                    q("What should an automated rollback use as its strongest signal?", 2, "A fixed five-minute timer", "User-impacting health and error thresholds", "The number of commits", "Developer availability"))),
            new ExamSeed("Docker, Linux and Git", "Practical container, command-line and version-control knowledge.", 20, List.of(
                    q("What is a Docker image?", 3, "A running process", "A virtual machine kernel", "An immutable template for containers", "A Git branch"),
                    q("What do containers on the same Linux host normally share?", 1, "The host kernel", "The application filesystem", "The same process ID", "All environment variables"),
                    q("Which Docker feature keeps database files beyond a container's lifetime?", 4, "A health check", "An image tag", "A build argument", "A volume"),
                    q("What does git rebase do to local commits?", 2, "Deletes them permanently", "Replays them onto a new base", "Publishes them as releases", "Encrypts their contents"),
                    q("What permissions does chmod 640 file.txt grant?", 1, "Owner read/write, group read, others none", "Everyone read/write", "Owner execute only", "Group and others write only"),
                    q("Which command commonly reads systemd service logs?", 3, "grepctl", "servicelog", "journalctl", "sysread"))),
            new ExamSeed("SQL and Data Fundamentals", "Relational modeling, querying, indexing and transaction safety.", 18, List.of(
                    q("What is the purpose of a primary key?", 2, "Sort every query", "Uniquely identify each row", "Encrypt a table", "Allow duplicate records"),
                    q("What does an INNER JOIN return?", 4, "Every row from both tables", "Only rows from the left table", "Only rows containing null", "Rows with matching join conditions"),
                    q("What is the typical tradeoff of adding an index?", 1, "Faster reads with extra storage and write cost", "Slower reads and faster writes", "Automatic data encryption", "Removal of uniqueness rules"),
                    q("Which ACID property means a transaction completes fully or not at all?", 3, "Consistency", "Isolation", "Atomicity", "Durability"),
                    q("Which clause groups rows before applying aggregate functions?", 2, "ORDER BY", "GROUP BY", "DISTINCT ON", "UNION ALL"),
                    q("What is the safest default defense against SQL injection in application queries?", 4, "Escaping spaces", "Hiding database errors", "Renaming columns", "Parameterized queries")))
    );

    private final UserRepository users;
    private final ExamRepository exams;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(UserRepository users, ExamRepository exams, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.exams = exams;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUser(ADMIN_EMAIL, "Nguyễn Phú Triều (Admin)", User.ROLE_ADMIN);
        seedUser(STUDENT_EMAIL, "Nguyễn Phú Triều", User.ROLE_STUDENT);
        EXAM_SEEDS.stream()
                .filter(seed -> !exams.existsByTitleIgnoreCase(seed.title()))
                .map(this::createExam)
                .forEach(exams::save);
        log.info("Demo workspace ready: {} exams, student {}", EXAM_SEEDS.size(), STUDENT_EMAIL);
    }

    private void seedUser(String email, String fullName, String role) {
        if (users.findByUsernameIgnoreCase(email).isPresent()) return;
        User user = new User();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
        user.setFullName(fullName);
        user.setMobileNumber("");
        user.setRole(role);
        users.save(user);
    }

    private Exam createExam(ExamSeed seed) {
        Exam exam = new Exam();
        exam.setTitle(seed.title());
        exam.setDescription(seed.description());
        exam.setDurationInMinutes(seed.durationMinutes());
        seed.questions().forEach(questionSeed -> {
            Question question = new Question();
            question.setText(questionSeed.text());
            question.setOption1(questionSeed.options().get(0));
            question.setOption2(questionSeed.options().get(1));
            question.setOption3(questionSeed.options().get(2));
            question.setOption4(questionSeed.options().get(3));
            question.setCorrectAnswer(questionSeed.correctAnswer());
            question.setMarks(1);
            question.setExam(exam);
            exam.getQuestions().add(question);
        });
        return exam;
    }

    private static QuestionSeed q(String text, int correctAnswer, String... options) {
        return new QuestionSeed(text, List.of(options), correctAnswer);
    }

    private record ExamSeed(String title, String description, int durationMinutes, List<QuestionSeed> questions) {}
    private record QuestionSeed(String text, List<String> options, int correctAnswer) {}
}
