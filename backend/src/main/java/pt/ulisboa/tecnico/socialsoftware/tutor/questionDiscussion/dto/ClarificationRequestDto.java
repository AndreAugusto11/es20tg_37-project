package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest;

import java.io.Serializable;

public class ClarificationRequestDto implements Serializable {
    private Integer id;
    private String content;
    private QuestionAnswerDto questionAnswerDto;
    private String name;
    private String username;
    private String status;

    public ClarificationRequestDto() {
    }

    public ClarificationRequestDto(ClarificationRequest clarificationRequest) {
        this.id = clarificationRequest.getId();
        this.content = clarificationRequest.getContent();
        this.questionAnswerDto = new QuestionAnswerDto(clarificationRequest.getQuestionAnswer());
        this.name = clarificationRequest.getUser().getName();
        this.username = clarificationRequest.getUser().getUsername();
        this.status = clarificationRequest.getStatus().name();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public QuestionAnswerDto getQuestionAnswer() { return questionAnswerDto; }

    public void setQuestionAnswer(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswerDto = questionAnswerDto;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}
