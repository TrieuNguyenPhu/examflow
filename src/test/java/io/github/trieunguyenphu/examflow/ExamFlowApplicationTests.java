package io.github.trieunguyenphu.examflow;

import io.github.trieunguyenphu.examflow.model.Exam;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import io.github.trieunguyenphu.examflow.model.Question;
import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.ExamRepository;
import io.github.trieunguyenphu.examflow.repository.ExamResultRepository;
import io.github.trieunguyenphu.examflow.repository.QuestionRepository;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExamFlowApplicationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ExamRepository exams;
    @Autowired
    private ExamResultRepository results;
    @Autowired
    private QuestionRepository questions;
    @Autowired
    private UserRepository users;

    @Test
    void publicPagesRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/register")).andExpect(status().isOk());
    }

    @Test
    void anonymousUsersCannotAccessAdministration() throws Exception {
        mvc.perform(get("/admin/dashboard")).andExpect(status().is3xxRedirection());
    }

    @Test
    void studentsCannotAccessAdministration() throws Exception {
        mvc.perform(get("/admin/dashboard").with(user("student@example.com").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void postRequestsRequireCsrf() throws Exception {
        mvc.perform(post("/register")).andExpect(status().isForbidden());
        mvc.perform(post("/register").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("aria-invalid=\"true\"")))
                .andExpect(content().string(containsString("id=\"fullName-error\"")));
    }

    @Test
    @Transactional
    void authenticatedWorkspacesAndExamDeadlineRender() throws Exception {
        Exam exam = new Exam();
        exam.setTitle("Rendering check");
        exam.setDescription("Exercises all authenticated templates.");
        exam.setDurationInMinutes(30);
        exam = exams.save(exam);

        Question question = new Question();
        question.setExam(exam);
        question.setText("Which option is correct?");
        question.setOption1("First");
        question.setOption2("Second");
        question.setOption3("Third");
        question.setOption4("Fourth");
        question.setCorrectAnswer(1);
        question.setMarks(1);
        question = questions.save(question);
        exam.getQuestions().add(question);

        User student = new User();
        student.setUsername("rendering.student@example.com");
        student.setPassword("not-used-in-this-test");
        student.setFullName("Rendering Student");
        student.setMobileNumber("");
        student.setRole(User.ROLE_STUDENT);
        users.save(student);

        var admin = user("rendering.admin@example.com").roles("ADMIN");
        mvc.perform(get("/admin/dashboard").with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/manage-exams").with(admin))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("aria-current=\"page\"")));
        mvc.perform(get("/admin/exam/add").with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/exam/edit/{id}", exam.getId()).with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/exam/manage-questions/{id}", exam.getId()).with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/exam/{id}/question/add", exam.getId()).with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/question/edit/{id}", question.getId()).with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/results/{id}", exam.getId()).with(admin)).andExpect(status().isOk());
        mvc.perform(get("/admin/students").with(admin)).andExpect(status().isOk());

        var studentAuth = user(student.getUsername()).roles("STUDENT");
        mvc.perform(get("/student/dashboard").with(studentAuth)).andExpect(status().isOk());
        mvc.perform(get("/student/my-results").with(studentAuth)).andExpect(status().isOk());
        mvc.perform(get("/student/profile").with(studentAuth)).andExpect(status().isOk());
        mvc.perform(get("/exam/{id}", exam.getId()).with(studentAuth))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-deadline=\"")));
    }

    @Test
    @Transactional
    void examSummariesCountQuestionsAndExcludeCompletedExams() {
        Exam exam = new Exam();
        exam.setTitle("Repository summary check");
        exam.setDescription("Loads only the fields required by dashboard cards.");
        exam.setDurationInMinutes(20);
        exam = exams.save(exam);
        Long examId = exam.getId();

        Question question = new Question();
        question.setExam(exam);
        question.setText("Which query shape is smallest?");
        question.setOption1("Summary projection");
        question.setOption2("Full entity graph");
        question.setOption3("One query per row");
        question.setOption4("Load every answer");
        question.setCorrectAnswer(1);
        question.setMarks(1);
        questions.save(question);

        User student = new User();
        student.setUsername("summary.student@example.com");
        student.setPassword("not-used-in-this-test");
        student.setFullName("Summary Student");
        student.setMobileNumber("");
        student.setRole(User.ROLE_STUDENT);
        student = users.save(student);

        ExamResult result = new ExamResult();
        result.setExam(exam);
        result.setStudent(student);
        result.setScoreAchieved(1);
        result.setTotalMarks(1);
        result.setSubmissionTime(LocalDateTime.now());
        results.save(result);

        ExamRepository.ExamSummary summary = exams.summarizeExams().stream()
                .filter(candidate -> candidate.getId().equals(examId))
                .findFirst()
                .orElseThrow();

        assertThat(summary.getQuestionCount()).isEqualTo(1);
        assertThat(exams.findAvailableForStudent(student.getId()))
                .extracting(ExamRepository.ExamSummary::getId)
                .doesNotContain(examId);
    }
}
