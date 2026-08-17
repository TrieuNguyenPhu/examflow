package io.github.trieunguyenphu.examflow.controller;

import io.github.trieunguyenphu.examflow.model.Exam;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import io.github.trieunguyenphu.examflow.model.Question;
import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.ExamRepository;
import io.github.trieunguyenphu.examflow.repository.ExamResultRepository;
import io.github.trieunguyenphu.examflow.repository.QuestionRepository;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import io.github.trieunguyenphu.examflow.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ExamRepository exams;
    private final QuestionRepository questions;
    private final ExamResultRepository results;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService files;

    public AdminController(
            ExamRepository exams,
            QuestionRepository questions,
            ExamResultRepository results,
            UserRepository users,
            PasswordEncoder passwordEncoder,
            FileStorageService files) {
        this.exams = exams;
        this.questions = questions;
        this.results = results;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.files = files;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<ExamRepository.ExamSubmissionSummary> summaries = exams.summarizeSubmissions();
        model.addAttribute("totalStudents", users.countByRole(User.ROLE_STUDENT));
        model.addAttribute("totalExams", exams.count());
        model.addAttribute("totalQuestions", questions.count());
        model.addAttribute("totalSubmissions", results.count());
        model.addAttribute("submissionSummaries", summaries);
        model.addAttribute("recentResults", results.findTop5ByOrderBySubmissionTimeDesc());
        return "admin/dashboard";
    }

    @GetMapping("/manage-exams")
    public String manageExams(Model model) {
        model.addAttribute("exams", exams.summarizeExams());
        return "admin/manage_exams";
    }

    @GetMapping("/exam/add")
    public String addExamForm(Model model) {
        model.addAttribute("exam", new Exam());
        return "admin/add_exam";
    }

    @PostMapping("/exam/add")
    public String addExam(@Valid @ModelAttribute Exam exam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "admin/add_exam";
        exams.save(exam);
        return "redirect:/admin/manage-exams";
    }

    @GetMapping("/exam/edit/{examId}")
    public String editExamForm(@PathVariable Long examId, Model model) {
        model.addAttribute("exam", requireExam(examId));
        return "admin/edit_exam";
    }

    @PostMapping("/exam/update/{examId}")
    public String updateExam(
            @PathVariable Long examId,
            @Valid @ModelAttribute("exam") Exam form,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "admin/edit_exam";
        Exam exam = requireExam(examId);
        exam.setTitle(form.getTitle());
        exam.setDescription(form.getDescription());
        exam.setDurationInMinutes(form.getDurationInMinutes());
        exams.save(exam);
        return "redirect:/admin/manage-exams";
    }

    @PostMapping("/exam/delete/{examId}")
    public String deleteExam(@PathVariable Long examId, RedirectAttributes redirectAttributes) {
        if (!exams.existsById(examId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        exams.deleteById(examId);
        redirectAttributes.addFlashAttribute("successMessage", "Exam deleted.");
        return "redirect:/admin/manage-exams";
    }

    @GetMapping("/exam/{examId}/question/add")
    public String addQuestionForm(@PathVariable Long examId, Model model) {
        Exam exam = requireExam(examId);
        Question question = new Question();
        question.setExam(exam);
        model.addAttribute("question", question);
        model.addAttribute("exam", exam);
        return "admin/add_question";
    }

    @PostMapping("/exam/question/add")
    public String addQuestion(
            @Valid @ModelAttribute Question question,
            BindingResult bindingResult,
            @RequestParam Long examId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Exam exam = requireExam(examId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("exam", exam);
            return "admin/add_question";
        }
        question.setExam(exam);
        questions.save(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question added.");
        return "redirect:/admin/exam/" + examId + "/question/add";
    }

    @GetMapping("/exam/manage-questions/{examId}")
    public String manageQuestions(@PathVariable Long examId, Model model) {
        Exam exam = exams.findWithQuestionsById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("exam", exam);
        model.addAttribute("questions", exam.getQuestions());
        return "admin/manage_questions";
    }

    @GetMapping("/question/edit/{questionId}")
    public String editQuestionForm(@PathVariable Long questionId, Model model) {
        model.addAttribute("question", requireQuestion(questionId));
        return "admin/edit_question";
    }

    @PostMapping("/question/update/{questionId}")
    public String updateQuestion(
            @PathVariable Long questionId,
            @Valid @ModelAttribute("question") Question form,
            BindingResult bindingResult) {
        Question question = requireQuestion(questionId);
        if (bindingResult.hasErrors()) {
            form.setId(questionId);
            form.setExam(question.getExam());
            return "admin/edit_question";
        }
        question.setText(form.getText());
        question.setOption1(form.getOption1());
        question.setOption2(form.getOption2());
        question.setOption3(form.getOption3());
        question.setOption4(form.getOption4());
        question.setCorrectAnswer(form.getCorrectAnswer());
        question.setMarks(form.getMarks());
        questions.save(question);
        return "redirect:/admin/exam/manage-questions/" + question.getExam().getId();
    }

    @PostMapping("/exam/{examId}/question/delete/{questionId}")
    public String deleteQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            RedirectAttributes redirectAttributes) {
        Question question = requireQuestion(questionId);
        if (!question.getExam().getId().equals(examId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try {
            questions.delete(question);
            questions.flush();
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "This question is part of a completed submission and cannot be deleted.");
        }
        return "redirect:/admin/exam/manage-questions/" + examId;
    }

    @GetMapping("/results/{examId}")
    public String viewResults(@PathVariable Long examId, Model model) {
        Exam exam = requireExam(examId);
        List<ExamResult> examResults = results.findByExamOrderBySubmissionTimeDesc(exam);
        model.addAttribute("exam", exam);
        model.addAttribute("results", examResults);
        return "admin/view_results";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", users.findByRoleOrderByFullName(User.ROLE_STUDENT));
        return "admin/manage_students";
    }

    @PostMapping("/student/delete/{id}")
    @Transactional
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User student = users.findById(id)
                .filter(user -> User.ROLE_STUDENT.equals(user.getRole()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String profileImage = student.getProfilePicUrl();
        results.deleteByStudent(student);
        results.flush();
        users.delete(student);
        files.delete(profileImage);
        redirectAttributes.addFlashAttribute("successMessage", "Student account and submissions deleted.");
        return "redirect:/admin/students";
    }

    @PostMapping("/student/reset-password")
    public String resetStudentPassword(
            @RequestParam("userId") Long userId,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        if (newPassword.length() < 12 || newPassword.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must contain 12 to 100 characters.");
            return "redirect:/admin/students";
        }
        User student = users.findById(userId)
                .filter(user -> User.ROLE_STUDENT.equals(user.getRole()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        student.setPassword(passwordEncoder.encode(newPassword));
        users.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "Password reset for " + student.getUsername() + ".");
        return "redirect:/admin/students";
    }

    private Exam requireExam(Long id) {
        return exams.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private Question requireQuestion(Long id) {
        return questions.findWithExamById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
