package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "clarification_request_answers")
public class ClarificationRequestAnswer {
    public enum Type {TEACHER , STUDENT}

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


    public ClarificationRequestAnswer() {
    }


    public ClarificationRequestAnswer(ClarificationRequest clarificationRequest, User user, String content) {
        this.clarificationRequest = clarificationRequest;
        this.user = user;
        this.content = content;

        if (user.getRole() == User.Role.STUDENT) {this.type = Type.STUDENT; }
        if (user.getRole() == User.Role.TEACHER) {this.type = Type.TEACHER; }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ClarificationRequest getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}