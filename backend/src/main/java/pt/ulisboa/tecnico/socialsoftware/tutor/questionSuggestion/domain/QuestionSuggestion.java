package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;



@Entity
@Table(name = "question_suggestions")
public class QuestionSuggestion {
    @SuppressWarnings("unused")
    public enum Status {
        REJECTED, ACCEPTED, PENDING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private QuestionSuggestion.Status status = Status.PENDING;

    @OneToOne
    @JoinColumn(name="question_id")
    private Question question;

    @OneToOne
    @JoinColumn(name="justification_id")
    private Justification justification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public QuestionSuggestion() {
    }

    public QuestionSuggestion(User user, Course course, QuestionSuggestionDto questionSuggestionDto) {

        this.question = new Question(course, questionSuggestionDto.getQuestionDto());
        this.question.setStatus(Question.Status.PENDING);
        setCreationDate(DateHandler.toLocalDateTime(questionSuggestionDto.getCreationDate()));

        this.user = user;
        user.addQuestionSuggestion(this);

        this.status = QuestionSuggestion.Status.valueOf(questionSuggestionDto.getStatus());
    }

    public void update(QuestionSuggestionDto questionSuggestionDto) {

        this.getQuestion().update(questionSuggestionDto.getQuestionDto());
        this.setStatus(Status.PENDING);
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitle(){ return question.getTitle(); }

    public void setTitle(String title){ this.question.setTitle(title); }

    public String getContent(){ return question.getContent(); }

    public void setContent(String content){ this.question.setContent(content); }

    public QuestionSuggestion.Status getStatus() { return status; }

    public void setStatus(QuestionSuggestion.Status status) { this.status = status; }

    public Image getImage(){ return question.getImage(); }

    public void setImage(Image image){ this.question.setImage(image);}

    public List<Option> getOptions(){ return question.getOptions(); }

    public LocalDateTime getCreationDate(){ return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now();
        } else {
            this.creationDate = creationDate;
        }
    }

    public Question getQuestion(){ return question; }

    public void setQuestion(Question question) { this.question = question;}

    public User getUser(){ return user; }

    public void setUser(User user){ this.user = user; }

    public Course getCourse(){ return question.getCourse(); }

    public void setCourse(Course course){ this.question.setCourse(course); }

    public Justification getJustification() { return justification; }

    public void setJustification(Justification justification) { this.justification = justification; }

    public void addOption(Option option){ question.addOption(option); }

    public void setOptions(List<OptionDto> options){ this.question.setOptions(options); }
}


