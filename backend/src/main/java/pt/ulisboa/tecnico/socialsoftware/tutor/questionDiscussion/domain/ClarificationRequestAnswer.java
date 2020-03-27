package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "clarification_request_answers")
public class ClarificationRequestAnswer {
    public enum Type {TEACHER_ANSWER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToOne(mappedBy = "clarificationRequestAnswer")
    private ClarificationRequest clarificationRequest;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;


    public ClarificationRequestAnswer() { }

    public ClarificationRequestAnswer(ClarificationRequest clarificationRequest, Type type, User user, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new TutorException(CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY);
        }

        if (clarificationRequest.getStatus() == ClarificationRequest.Status.CLOSED) {
            throw new TutorException(CLARIFICATION_REQUEST_NO_LONGER_AVAILABLE);
        }

        if (!clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution().getUsers().contains(user)) {
            // For now assuming only the teachers in that course execution can create answers to the clarification requests
            throw new TutorException(ACCESS_DENIED);
        }

        this.clarificationRequest = clarificationRequest;
        this.user = user;
        this.content = content;
        this.type = type;
        clarificationRequest.setClarificationRequestAnswer(this);
        user.addClarificationRequestAnswers(this);

        this.clarificationRequest.setStatus(ClarificationRequest.Status.CLOSED);
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
}