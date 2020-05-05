package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.PublicClarificationRequest;

public class PublicClarificationRequestDto {
    private Integer id;
    private Integer clarificationRequestId;

    public PublicClarificationRequestDto() {
    }

    public PublicClarificationRequestDto(PublicClarificationRequest publicClarificationRequest) {
        this.id = publicClarificationRequest.getId();
        this.clarificationRequestId = publicClarificationRequest.getClarificationRequest().getId();
    }

    public Integer getId() {
        return id;
    }

    public Integer getClarificationRequestId() {
        return clarificationRequestId;
    }

    public void setClarificationRequestId(Integer clarificationRequestDto) {
        this.clarificationRequestId = clarificationRequestDto;
    }
}
