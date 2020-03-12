package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequestAnswer;

public class ClarificationRequestAnswerDto {
    private Integer id;
    private ClarificationRequestAnswer.Type type;
    private String content;
    private ClarificationRequestDto clarificationRequestDto;
    private String name;
    private String username;

    public ClarificationRequestAnswerDto() {
    }

    public ClarificationRequestAnswerDto(ClarificationRequestAnswer clarificationRequestAnswer) {
        this.id = clarificationRequestAnswer.getId();
        this.type = clarificationRequestAnswer.getType();
        this.content = clarificationRequestAnswer.getContent();
        this.clarificationRequestDto = new ClarificationRequestDto(clarificationRequestAnswer.getClarificationRequest());
        this.username = clarificationRequestAnswer.getUser().getUsername();
        this.content = clarificationRequestAnswer.getContent();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClarificationRequestAnswer.Type getType() {
        return type;
    }

    public void setType(ClarificationRequestAnswer.Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ClarificationRequestDto getClarificationRequest() {
        return clarificationRequestDto;
    }

    public void setClarificationRequest(ClarificationRequestDto clarificationRequestDto) {
        this.clarificationRequestDto = clarificationRequestDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
