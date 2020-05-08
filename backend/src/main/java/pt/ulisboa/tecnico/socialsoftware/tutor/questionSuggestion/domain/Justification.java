package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "justifications")
public class Justification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToOne
    @JoinColumn(name="suggestion_id")
    private QuestionSuggestion questionSuggestion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "justification")
    private Image image;

    public Justification() {
    }

    public Justification(User user, QuestionSuggestion questionSuggestion, JustificationDto justificationDto) {

        this.key = justificationDto.getKey();
        this.content = justificationDto.getContent();

        this.user = user;
        user.addJustification(this);

        questionSuggestion.setJustification(this);

        if (justificationDto.getImage() != null) {
            Image img = new Image(justificationDto.getImage());
            setImage(img);
            img.setJustification(this);
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public QuestionSuggestion getQuestionSuggestion() { return questionSuggestion; }

    public void setQuestionSuggestion(QuestionSuggestion questionSuggestion) {
        this.questionSuggestion = questionSuggestion;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Image getImage() { return image; }

    public void setImage(Image image) { this.image = image; }
}
