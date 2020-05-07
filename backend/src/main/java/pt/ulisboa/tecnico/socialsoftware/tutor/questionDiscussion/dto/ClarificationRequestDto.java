package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest;

import java.io.Serializable;

public class ClarificationRequestDto implements Serializable {
    private Integer id;
    private String content;
    private QuestionAnswerDto questionAnswerDto;
    private String name;
    private String username;
    private String status;
    private ImageDto image;
    private Integer numberOfAnswers;
    private boolean isPublic;
    private String creationDate;

    public ClarificationRequestDto() {
    }

    public ClarificationRequestDto(ClarificationRequest clarificationRequest) {
        this.id = clarificationRequest.getId();
        this.content = clarificationRequest.getContent();
        this.questionAnswerDto = new QuestionAnswerDto(clarificationRequest.getQuestionAnswer());
        this.name = clarificationRequest.getUser().getName();
        this.username = clarificationRequest.getUser().getUsername();
        this.status = clarificationRequest.getStatus().name();
        this.numberOfAnswers = clarificationRequest.getClarificationRequestAnswer().size();
        this.creationDate = DateHandler.toISOString(clarificationRequest.getCreationDate());

        this.isPublic = clarificationRequest.getPublicClarificationRequest() != null;

        if (clarificationRequest.getImage() != null)
            this.image = new ImageDto(clarificationRequest.getImage());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public QuestionAnswerDto getQuestionAnswerDto() { return questionAnswerDto; }

    public void setQuestionAnswerDto(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswerDto = questionAnswerDto;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public ImageDto getImage() { return image; }

    public void setImage(ImageDto image) { this.image = image; }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        isPublic = isPublic;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}