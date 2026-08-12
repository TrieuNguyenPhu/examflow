package io.github.trieunguyenphu.examflow.controller;

import io.github.trieunguyenphu.examflow.model.Exam;
import io.github.trieunguyenphu.examflow.model.ExamAnswer;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import io.github.trieunguyenphu.examflow.model.Question;
import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.ExamRepository;
import io.github.trieunguyenphu.examflow.repository.ExamResultRepository;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/exam")
public class ExamController {

    private static final String STARTED_AT_PREFIX = "exam-started-at:";
    private final ExamRepository exams;
    private final ExamResultRepository results;
    private final UserRepository users;

    public ExamController(ExamRepository exams, ExamResultRepository results, UserRepository users) {
        this.exams = exams;
        this.results = results;
        this.users = users;
    }

    @GetMapping("/{examId}")
    public String examPage(
            @PathVariable Long examId,
            Authentication authentication,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        User student = currentStudent(authentication);
        Exam exam = exams.findWithQuestionsById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (results.existsByStudentAndExam(student, exam)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already completed this exam.");
            return "redirect:/student/dashboard";
        }
        if (exam.getQuestions().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This exam has no questions yet.");
            return "redirect:/student/dashboard";
        }

        if (session.getAttribute(STARTED_AT_PREFIX + examId) == null) {
            session.setAttribute(STARTED_AT_PREFIX + examId, Instant.now());
        }
        Instant startedAt = (Instant) session.getAttribute(STARTED_AT_PREFIX + examId);
        model.addAttribute("student", student);
        model.addAttribute("exam", exam);
        model.addAttribute("questions", exam.getQuestions());
        model.addAttribute("deadlineEpochMillis", startedAt.plus(Duration.ofMinutes(exam.getDurationInMinutes())).toEpochMilli());
        return "student/exam_page";
    }

    @PostMapping("/submit")
    @Transactional
    public String submitExam(
            @RequestParam Long examId,
            @RequestParam Map<String, String> submittedAnswers,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User student = currentStudent(authentication);
        Exam exam = exams.findWithQuestionsById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (results.existsByStudentAndExam(student, exam)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already completed this exam.");
            return "redirect:/student/dashboard";
        }

        Object startedValue = session.getAttribute(STARTED_AT_PREFIX + examId);
        if (!(startedValue instanceof Instant startedAt)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your exam session expired. Please start again.");
            return "redirect:/student/dashboard";
        }
        Duration elapsed = Duration.between(startedAt, Instant.now());
        if (elapsed.compareTo(Duration.ofMinutes(exam.getDurationInMinutes() + 2L)) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "The submission window has expired.");
            session.removeAttribute(STARTED_AT_PREFIX + examId);
            return "redirect:/student/dashboard";
        }

        ExamResult result = new ExamResult();
        result.setStudent(student);
        result.setExam(exam);
        result.setSubmissionTime(LocalDateTime.now());

        int totalMarks = 0;
        int score = 0;
        for (Question question : exam.getQuestions()) {
            totalMarks += question.getMarks();
            int selected = parseOption(submittedAnswers.get("q_" + question.getId()));
            if (selected == question.getCorrectAnswer()) score += question.getMarks();
            result.addAnswer(new ExamAnswer(result, question, selected));
        }

        result.setScoreAchieved(score);
        result.setTotalMarks(totalMarks);
        results.save(result);
        session.removeAttribute(STARTED_AT_PREFIX + examId);
        return "redirect:/student/result/" + result.getId();
    }

    private int parseOption(String value) {
        try {
            int option = Integer.parseInt(value);
            return option >= 1 && option <= 4 ? option : 0;
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private User currentStudent(Authentication authentication) {
        return users.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
