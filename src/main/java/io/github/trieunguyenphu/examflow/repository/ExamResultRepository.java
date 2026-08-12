package io.github.trieunguyenphu.examflow.repository;

import io.github.trieunguyenphu.examflow.model.Exam;
import io.github.trieunguyenphu.examflow.model.ExamResult;
import io.github.trieunguyenphu.examflow.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    @EntityGraph(attributePaths = {"student", "exam"})
    List<ExamResult> findByExamOrderBySubmissionTimeDesc(Exam exam);

    @EntityGraph(attributePaths = "exam")
    List<ExamResult> findByStudentOrderBySubmissionTimeDesc(User student);

    @EntityGraph(attributePaths = {"student", "exam"})
    List<ExamResult> findTop5ByOrderBySubmissionTimeDesc();

    @EntityGraph(attributePaths = {"student", "exam", "exam.questions"})
    @Query("select r from ExamResult r where r.id = :id")
    Optional<ExamResult> findDetailedById(Long id);

    boolean existsByStudentAndExam(User student, Exam exam);

    @Transactional
    void deleteByStudent(User student);
}
