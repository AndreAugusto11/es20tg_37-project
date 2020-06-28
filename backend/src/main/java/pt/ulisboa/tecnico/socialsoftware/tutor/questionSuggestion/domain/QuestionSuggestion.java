package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_DELETE_COURSE_EXECUTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_DELETE_QUESTION_SUGGESTION;


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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "questionSuggestion", orphanRemoval=true)
    private Question question;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "questionSuggestion", orphanRemoval=true)
    private Justification justification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public QuestionSuggestion() {
    }

    public QuestionSuggestion(User user, Course course, QuestionSuggestionDto questionSuggestionDto) {
        setQuestion(new Question(course, questionSuggestionDto.getQuestionDto()));
        setCreationDate(DateHandler.toLocalDateTime(questionSuggestionDto.getCreationDate()));
        setStatus(QuestionSuggestion.Status.valueOf(questionSuggestionDto.getStatus()));
        setUser(user);
    }

    public void update(QuestionSuggestionDto questionSuggestionDto) {
        this.getQuestion().update(questionSuggestionDto.getQuestionDto());
        this.setStatus(Status.PENDING);
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() {
        return this.question.getKey();
    }

    public String getTitle() { return question.getTitle(); }

    public void setTitle(String title) { this.question.setTitle(title); }

    public String getContent() { return question.getContent(); }

    public void setContent(String content) { this.question.setContent(content); }

    public QuestionSuggestion.Status getStatus() { return status; }

    public void setStatus(QuestionSuggestion.Status status) { this.status = status; }

    public Image getImage(){ return question.getImage(); }

    public void setImage(Image image){ this.question.setImage(image); }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now().plusHours(1);
        } else {
            this.creationDate = creationDate;
        }
    }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) {
        this.question = question;
        this.question.setQuestionSuggestion(this);
    }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
        this.user.addQuestionSuggestion(this);
    }

    public Course getCourse() {
        return question.getCourse();
    }

    public void setCourse(Course course){ this.question.setCourse(course); }

    public Justification getJustification() { return justification; }

    public void setJustification(Justification justification) {
        this.justification = justification;
        this.justification.setQuestionSuggestion(this);
    }

    public void addOption(Option option){ question.addOption(option); }

    public List<Option> getOptions(){ return question.getOptions(); }

    public void setOptions(List<OptionDto> options){ this.question.setOptions(options); }

    public List<OptionDto> dupOptions() {
        List<OptionDto> options = new ArrayList<>();

        for (Option op : getOptions()) {
            OptionDto option = new OptionDto();
            option.setContent(op.getContent());
            option.setCorrect(op.getCorrect());
            options.add(option);
        }
        return options;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "id=" + id +
                ", status=" + status +
                ", user=" + user.getId() +
                ", question=" + question +
                ", justification=" + justification +
                '}';
    }

    public void remove() {
        if (status == Status.PENDING)
            throw new TutorException(CANNOT_DELETE_QUESTION_SUGGESTION);

        user.getQuestionsSuggestion().remove(this);
        user = null;

        question.remove();

        if (justification != null)
            justification.remove();
    }
}


