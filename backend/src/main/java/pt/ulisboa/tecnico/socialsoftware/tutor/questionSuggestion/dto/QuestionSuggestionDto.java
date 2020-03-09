package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionSuggestionDto {

    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private String creationDate = null;
    private String status;
    private List<OptionDto> options = new ArrayList<>();
    private ImageDto image;
    private Integer sequence;

    public QuestionSuggestionDto() {
    }

    public QuestionSuggestionDto(QuestionSuggestion questionSuggestion) {
        this.id = questionSuggestion.getId();
        this.key = questionSuggestion.getKey();
        this.title = questionSuggestion.getTitle();
        this.content = questionSuggestion.getContent();
        this.status = questionSuggestion.getStatus().name();
        this.options = questionSuggestion.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());

        if (questionSuggestion.getImage() != null)
            this.image = new ImageDto(questionSuggestion.getImage());
        if (questionSuggestion.getCreationDate() != null)
            this.creationDate = questionSuggestion.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", options=" + options +
                ", image=" + image +
                ", sequence=" + sequence +
                '}';
    }
}

