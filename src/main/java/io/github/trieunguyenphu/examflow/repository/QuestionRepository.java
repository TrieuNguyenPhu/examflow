package io.github.trieunguyenphu.examflow.repository;

import io.github.trieunguyenphu.examflow.model.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = "exam")
    Optional<Question> findWithExamById(Long id);
}
