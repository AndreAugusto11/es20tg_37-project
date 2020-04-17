package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuestionSuggestionDto implements Serializable {

    private Integer id;
    private QuestionDto questionDto;
    private String status;
    private String creationDate = null;
    private JustificationDto justificationDto = null;

    public QuestionSuggestionDto() {
    }

    public QuestionSuggestionDto(QuestionSuggestion questionSuggestion) {
        this.id = questionSuggestion.getId();
        this.questionDto = new QuestionDto(questionSuggestion.getQuestion());
        this.status = questionSuggestion.getStatus().name();

        if (questionSuggestion.getCreationDate() != null) {
            this.creationDate = questionSuggestion.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        if (questionSuggestion.getJustification() != null) {
            this.justificationDto = new JustificationDto(questionSuggestion.getJustification());
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitle(){ return questionDto.getTitle(); }

    public void setTitle(String title){ this.questionDto.setTitle(title);}

    public String getContent(){ return questionDto.getContent(); }

    public void setContent(String content){ this.questionDto.setContent(content); }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public ImageDto getImage(){ return questionDto.getImage(); }

    public void setImage(ImageDto image){ this.questionDto.setImage(image); }

    public List<OptionDto> getOptions(){ return questionDto.getOptions(); }

    public void setOptions(List<OptionDto> options){ this.questionDto.setOptions(options); }

    public Integer getSequence(){ return questionDto.getSequence(); }

    public void setSequence(Integer sequence){ this.questionDto.setSequence(sequence); }

    public String getCreationDate(){ return creationDate; }

    public void setCreationDate(String creationDate){ this.creationDate = creationDate; }

    public QuestionDto getQuestionDto(){ return questionDto; }

    public void setQuestionDto(QuestionDto questionDto){ this.questionDto = questionDto; }

    public JustificationDto getJustificationDto() { return justificationDto; }

    public void setJustificationDto(JustificationDto justificationDto) { this.justificationDto = justificationDto; }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", status=" + status +
                ", question=" + questionDto +
                ", creation date=" + creationDate +
                ", justification=" + justificationDto +
                '}';
    }
}

