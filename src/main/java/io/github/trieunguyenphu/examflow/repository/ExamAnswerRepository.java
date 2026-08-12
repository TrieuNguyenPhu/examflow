package io.github.trieunguyenphu.examflow.repository;

import io.github.trieunguyenphu.examflow.model.ExamAnswer;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {
    @EntityGraph(attributePaths = "question")
    List<ExamAnswer> findByExamResult(ExamResult examResult);
}
