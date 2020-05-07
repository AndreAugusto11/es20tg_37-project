package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequestAnswer;

public class ClarificationRequestAnswerDto {
    private Integer id;
    private ClarificationRequestAnswer.Type type;
    private String content;
    private String name;
    private String username;
    private String creationDate;

    public ClarificationRequestAnswerDto() { }

    public ClarificationRequestAnswerDto(ClarificationRequestAnswer clarificationRequestAnswer) {
        this.id = clarificationRequestAnswer.getId();
        this.type = clarificationRequestAnswer.getType();
        this.content = clarificationRequestAnswer.getContent();
        this.name = clarificationRequestAnswer.getUser().getName();
        this.username = clarificationRequestAnswer.getUser().getUsername();
        this.creationDate = DateHandler.toISOString(clarificationRequestAnswer.getCreationDate());
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
