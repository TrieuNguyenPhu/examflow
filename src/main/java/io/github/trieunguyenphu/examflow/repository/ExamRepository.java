package io.github.trieunguyenphu.examflow.repository;

import io.github.trieunguyenphu.examflow.model.Exam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    boolean existsByTitleIgnoreCase(String title);

    @EntityGraph(attributePaths = "questions")
    @Query("select distinct e from Exam e order by e.title")
    List<Exam> findAllWithQuestions();

    @EntityGraph(attributePaths = "questions")
    @Query("select e from Exam e where e.id = :id")
    Optional<Exam> findWithQuestionsById(Long id);

    @Query("""
            select e.id as id,
                   e.title as title,
                   e.description as description,
                   e.durationInMinutes as durationInMinutes,
                   count(q.id) as questionCount
            from Exam e left join e.questions q
            group by e.id, e.title, e.description, e.durationInMinutes
            order by e.title
            """)
    List<ExamSummary> summarizeExams();

    @Query("""
            select e.id as id,
                   e.title as title,
                   e.description as description,
                   e.durationInMinutes as durationInMinutes,
                   count(q.id) as questionCount
            from Exam e left join e.questions q
            where not exists (
                select r.id from ExamResult r
                where r.exam = e and r.student.id = :studentId
            )
            group by e.id, e.title, e.description, e.durationInMinutes
            order by e.title
            """)
    List<ExamSummary> findAvailableForStudent(Long studentId);

    @Query("""
            select e.title as title, count(r.id) as submissions
            from Exam e left join e.results r
            group by e.id, e.title
            order by e.title
            """)
    List<ExamSubmissionSummary> summarizeSubmissions();

    interface ExamSubmissionSummary {
        String getTitle();
        long getSubmissions();
    }

    interface ExamSummary {
        Long getId();
        String getTitle();
        String getDescription();
        int getDurationInMinutes();
        long getQuestionCount();
    }
}
