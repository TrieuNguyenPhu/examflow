package io.github.trieunguyenphu.examflow.config;

import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.ExamRepository;
import io.github.trieunguyenphu.examflow.repository.QuestionRepository;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:examflow-demo-test;DB_CLOSE_DELAY=-1")
@ActiveProfiles("demo")
class DemoDataInitializerTests {

    @Autowired
    private DemoDataInitializer initializer;
    @Autowired
    private ExamRepository exams;
    @Autowired
    private QuestionRepository questions;
    @Autowired
    private UserRepository users;

    @Test
    void seedsARepeatableTechnicalAssessmentWorkspace() throws Exception {
        assertThat(exams.count()).isEqualTo(5);
        assertThat(questions.count()).isEqualTo(30);
        assertThat(users.countByRole(User.ROLE_ADMIN)).isEqualTo(1);
        assertThat(users.countByRole(User.ROLE_STUDENT)).isEqualTo(1);

        initializer.run();

        assertThat(exams.count()).isEqualTo(5);
        assertThat(questions.count()).isEqualTo(30);
        assertThat(users.count()).isEqualTo(2);
    }
}
