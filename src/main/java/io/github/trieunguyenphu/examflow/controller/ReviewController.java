package io.github.trieunguyenphu.examflow.controller;

import io.github.trieunguyenphu.examflow.model.ExamAnswer;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import io.github.trieunguyenphu.examflow.repository.ExamAnswerRepository;
import io.github.trieunguyenphu.examflow.repository.ExamResultRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class ReviewController {

    private final ExamResultRepository results;
    private final ExamAnswerRepository answers;

    public ReviewController(ExamResultRepository results, ExamAnswerRepository answers) {
        this.results = results;
        this.answers = answers;
    }

    @GetMapping("/review/{resultId}")
    public String reviewExam(@PathVariable Long resultId, Authentication authentication, Model model) {
        ExamResult result = ownedResult(resultId, authentication);
        Map<Long, Integer> selectedAnswers = answers.findByExamResult(result).stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), ExamAnswer::getSelectedOption));

        model.addAttribute("exam", result.getExam());
        model.addAttribute("result", result);
        model.addAttribute("questions", result.getExam().getQuestions());
        model.addAttribute("studentAnswers", selectedAnswers);
        return "student/review_exam";
    }

    ExamResult ownedResult(Long resultId, Authentication authentication) {
        ExamResult result = results.findDetailedById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!result.getStudent().getUsername().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return result;
    }
}
