package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "questionSuggestions")
public class QuestionSuggestion {
    @SuppressWarnings("unused")
    public enum Status {
        REJECTED, AVAILABLE, PENDING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Enumerated(EnumType.STRING)
    private QuestionSuggestion.Status status = Status.PENDING;

    @OneToOne(cascade = CascadeType.ALL)
    private Image image;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany
    private List<Option> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public QuestionSuggestion() {
    }

    public QuestionSuggestion(User user, QuestionSuggestionDto questionSuggestionDto) {
        checkConsistentQuestionSuggestion(questionSuggestionDto);
        this.title = questionSuggestionDto.getTitle();
        this.key = questionSuggestionDto.getKey();
        this.content = questionSuggestionDto.getContent();
        this.status = QuestionSuggestion.Status.valueOf(questionSuggestionDto.getStatus());

        this.user = user;
        user.addQuestionSuggestion(this);

        if (questionSuggestionDto.getImage() != null) {
            Image img = new Image(questionSuggestionDto.getImage());
            setImage(img);
            img.setQuestionSuggestion(this);
        }

        int index = 0;
        for (OptionDto optionDto : questionSuggestionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setQuestionSuggestion(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QuestionSuggestion.Status getStatus() {
        return status;
    }

    public void setStatus(QuestionSuggestion.Status status) {
        this.status = status;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setQuestionSuggestion(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    private void checkConsistentQuestionSuggestion(QuestionSuggestionDto questionSuggestionDto) {
        if (questionSuggestionDto.getTitle().trim().length() == 0 ||
                questionSuggestionDto.getContent().trim().length() == 0 ||
                questionSuggestionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (questionSuggestionDto.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

    }

    public void setOptionsSequence() {
        int index = 0;
        for (Option option: getOptions()) {
            option.setSequence(index++);
        }
    }

}
