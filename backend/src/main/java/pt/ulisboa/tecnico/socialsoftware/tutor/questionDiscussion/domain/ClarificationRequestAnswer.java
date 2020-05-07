package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "clarification_request_answers")
public class ClarificationRequestAnswer {
    public enum Type { TEACHER_ANSWER, STUDENT_ANSWER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "clarification_request_id")
    private ClarificationRequest clarificationRequest;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public ClarificationRequestAnswer() { }

    public ClarificationRequestAnswer(ClarificationRequest clarificationRequest, Type type, User user, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new TutorException(CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY);
        }

        if (clarificationRequest.getStatus() == ClarificationRequest.Status.CLOSED) {
            throw new TutorException(CLARIFICATION_REQUEST_NO_LONGER_AVAILABLE);
        }

        if ((user.getRole().equals(User.Role.STUDENT) && !clarificationRequest.getUser().getId().equals(user.getId())) ||
                !clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution().getUsers().contains(user)) {
            throw new TutorException(ACCESS_DENIED);
        }

        this.clarificationRequest = clarificationRequest;
        this.user = user;
        this.content = content;
        this.type = type;
        clarificationRequest.addClarificationRequestAnswer(this);
        user.addClarificationRequestAnswers(this);
        this.creationDate = DateHandler.now();

        if (user.getRole().equals(User.Role.TEACHER)) {
            this.clarificationRequest.setStatus(ClarificationRequest.Status.ANSWERED);
        }
        else {
            this.clarificationRequest.setStatus(ClarificationRequest.Status.OPEN);
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Type getType() { return type; }

    public void setType(Type type) { this.type = type; }

    public ClarificationRequest getClarificationRequest() { return clarificationRequest; }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) { this.clarificationRequest = clarificationRequest; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now();
        } else {
            this.creationDate = creationDate;
        }
    }
}