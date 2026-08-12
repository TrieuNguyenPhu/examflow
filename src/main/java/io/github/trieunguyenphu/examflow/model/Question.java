package io.github.trieunguyenphu.examflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question text is required.")
    @Size(max = 1024, message = "Question text must be 1,024 characters or fewer.")
    @Column(nullable = false, length = 1024)
    private String text;

    @NotBlank(message = "Option 1 is required.")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String option1;

    @NotBlank(message = "Option 2 is required.")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String option2;

    @NotBlank(message = "Option 3 is required.")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String option3;

    @NotBlank(message = "Option 4 is required.")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String option4;

    @Min(value = 1, message = "Correct answer must be between 1 and 4.")
    @Max(value = 4, message = "Correct answer must be between 1 and 4.")
    @Column(nullable = false)
    private int correctAnswer;

    @Min(value = 1, message = "Marks must be at least 1.")
    @Max(value = 100, message = "Marks cannot exceed 100.")
    @Column(nullable = false)
    private int marks;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getOption1() { return option1; }
    public void setOption1(String option1) { this.option1 = option1; }
    public String getOption2() { return option2; }
    public void setOption2(String option2) { this.option2 = option2; }
    public String getOption3() { return option3; }
    public void setOption3(String option3) { this.option3 = option3; }
    public String getOption4() { return option4; }
    public void setOption4(String option4) { this.option4 = option4; }
    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }
    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
}
