package io.github.trieunguyenphu.examflow.controller;

import io.github.trieunguyenphu.examflow.model.Exam;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import io.github.trieunguyenphu.examflow.model.User;
import io.github.trieunguyenphu.examflow.repository.ExamRepository;
import io.github.trieunguyenphu.examflow.repository.ExamResultRepository;
import io.github.trieunguyenphu.examflow.repository.UserRepository;
import io.github.trieunguyenphu.examflow.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/student")
public class StudentController {

    private static final Pattern PHONE = Pattern.compile("^$|^[+0-9() .-]{7,30}$");
    private final UserRepository users;
    private final ExamRepository exams;
    private final ExamResultRepository results;
    private final FileStorageService files;
    private final PasswordEncoder passwordEncoder;

    public StudentController(
            UserRepository users,
            ExamRepository exams,
            ExamResultRepository results,
            FileStorageService files,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.exams = exams;
        this.results = results;
        this.files = files;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User student = currentStudent(authentication);
        List<ExamResult> pastResults = results.findByStudentOrderBySubmissionTimeDesc(student);

        int totalPossible = pastResults.stream().mapToInt(ExamResult::getTotalMarks).sum();
        int totalEarned = pastResults.stream().mapToInt(ExamResult::getScoreAchieved).sum();
        int highestScore = pastResults.stream().mapToInt(this::percentage).max().orElse(0);
        double averageScore = totalPossible == 0 ? 0 : totalEarned * 100.0 / totalPossible;

        List<ExamResult> chartResults = new ArrayList<>(pastResults);
        Collections.reverse(chartResults);
        Set<Long> takenExamIds = new HashSet<>();
        pastResults.forEach(result -> takenExamIds.add(result.getExam().getId()));
        List<Exam> availableExams = exams.findAllWithQuestions().stream()
                .filter(exam -> !takenExamIds.contains(exam.getId()))
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("pastResults", pastResults);
        model.addAttribute("totalTaken", pastResults.size());
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("highestScore", highestScore);
        model.addAttribute("chartResults", chartResults);
        model.addAttribute("availableExams", availableExams);
        return "student/dashboard";
    }

    @GetMapping("/my-results")
    public String myResults(Authentication authentication, Model model) {
        User student = currentStudent(authentication);
        model.addAttribute("pastResults", results.findByStudentOrderBySubmissionTimeDesc(student));
        return "student/my_results";
    }

    @GetMapping("/result/{resultId}")
    public String result(@PathVariable Long resultId, Authentication authentication, Model model) {
        ExamResult result = results.findDetailedById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!result.getStudent().getUsername().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("result", result);
        model.addAttribute("score", result.getScoreAchieved());
        model.addAttribute("total", result.getTotalMarks());
        model.addAttribute("resultId", result.getId());
        return "student/result";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("student", currentStudent(authentication));
        return "student/profile";
    }

    @PostMapping("/profile/update-details")
    public String updateDetails(
            @RequestParam String fullName,
            @RequestParam(defaultValue = "") String mobileNumber,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String cleanName = fullName.trim();
        String cleanPhone = mobileNumber.trim();
        if (cleanName.isEmpty() || cleanName.length() > 100 || !PHONE.matcher(cleanPhone).matches()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enter a valid name and phone number.");
            return "redirect:/student/profile";
        }
        User student = currentStudent(authentication);
        student.setFullName(cleanName);
        student.setMobileNumber(cleanPhone);
        users.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "Profile details updated.");
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/update-picture")
    public String updatePicture(
            @RequestParam("profilePicFile") MultipartFile profilePicFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User student = currentStudent(authentication);
            String oldPath = student.getProfilePicUrl();
            student.setProfilePicUrl(files.saveProfileImage(profilePicFile));
            users.save(student);
            files.delete(oldPath);
            redirectAttributes.addFlashAttribute("successMessage", "Profile picture updated.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/update-password")
    public String updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User student = currentStudent(authentication);
        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
        } else if (newPassword.length() < 12 || newPassword.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password must contain 12 to 100 characters.");
        } else if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
        } else if (passwordEncoder.matches(newPassword, student.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Choose a password you have not just used.");
        } else {
            student.setPassword(passwordEncoder.encode(newPassword));
            users.save(student);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated.");
        }
        return "redirect:/student/profile";
    }

    private int percentage(ExamResult result) {
        return result.getTotalMarks() == 0 ? 0 : (int) Math.round(result.getScoreAchieved() * 100.0 / result.getTotalMarks());
    }

    private User currentStudent(Authentication authentication) {
        return users.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
