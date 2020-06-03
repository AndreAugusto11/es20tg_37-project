package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Integer key;

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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarificationRequest")
    private PublicClarificationRequest publicClarificationRequest;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarificationRequest")
    private Image image;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

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
        this.creationDate = DateHandler.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public void setKey(Integer key) {
        this.key = key;
    }

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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

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

    public Integer getKey() {
        if (this.key == null)
            this.generateKeys();

        return key;
    }

    private void generateKeys() {
        int max = this.getQuestion().getClarificationRequest().stream()
                .filter(question -> question.key != null)
                .map(ClarificationRequest::getKey)
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(0);

        List<ClarificationRequest> nullKeyClarifications = this.getQuestion().getClarificationRequest().stream()
                .filter(question -> question.key == null).collect(Collectors.toList());

        for (ClarificationRequest clarificationRequest: nullKeyClarifications) {
            max = max + 1;
            clarificationRequest.setKey(max);
        }
    }
}
