package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "clarification_requests")
public class ClarificationRequest {
    public enum Status {
        CLOSED, OPEN, ANSWERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_answer_id")
    private QuestionAnswer questionAnswer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clarificationRequest", orphanRemoval=true)
    private Set<ClarificationRequestAnswer> clarificationRequestAnswer = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "public_clarification_request_id")
    private PublicClarificationRequest publicClarificationRequest;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarificationRequest")
    private Image image;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    public ClarificationRequest() {
    }

    public ClarificationRequest(QuestionAnswer questionAnswer, Question question, User user, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new TutorException(CLARIFICATION_REQUEST_IS_EMPTY);
        }

        this.questionAnswer = questionAnswer;
        questionAnswer.addClarificationRequest(this);
        this.question = question;
        question.addClarificationRequest(this);
        this.user = user;
        user.addClarificationRequest(this);
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public QuestionAnswer getQuestionAnswer() { return questionAnswer; }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) { this.questionAnswer = questionAnswer; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Image getImage() { return image; }

    public void setImage(Image image) { this.image = image; }

    public Set<ClarificationRequestAnswer> getClarificationRequestAnswer() { return this.clarificationRequestAnswer; }

    public void addClarificationRequestAnswer(ClarificationRequestAnswer clarificationRequestAnswer) {
        this.clarificationRequestAnswer.add(clarificationRequestAnswer);
    }

    public void closeClarificationRequest() {
        this.setStatus(Status.CLOSED);
    }

    public PublicClarificationRequest getPublicClarificationRequest() {
        return publicClarificationRequest;
    }

    public void setPublicClarificationRequest(PublicClarificationRequest publicClarificationRequest) {
        this.publicClarificationRequest = publicClarificationRequest;
    }
}
