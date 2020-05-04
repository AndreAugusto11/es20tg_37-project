package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.PublicClarificationRequest;

public class PublicClarificationRequestDto {
    private Integer id;
    private ClarificationRequestDto clarificationRequestDto;

    public PublicClarificationRequestDto() {

    }

    public PublicClarificationRequestDto(PublicClarificationRequest publicClarificationRequest) {
        this.clarificationRequestDto = new ClarificationRequestDto(publicClarificationRequest.getClarificationRequest());
    }

    public ClarificationRequestDto getClarificationRequestDto() {
        return clarificationRequestDto;
    }

    public void setClarificationRequestDto(ClarificationRequestDto clarificationRequestDto) {
        this.clarificationRequestDto = clarificationRequestDto;
    }
}
