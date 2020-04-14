package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class QuestionDiscussionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionDiscussionController.class);

    @Autowired
    QuestionDiscussionService questionDiscussionService;

    @PostMapping("/questionAnswers/{questionAnswerId}/clarificationRequests")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto createClarificationRequest(@PathVariable Integer questionAnswerId,
                                                              @Valid @RequestBody ClarificationRequestDto clarificationRequest) {
        return questionDiscussionService.createClarificationRequest(questionAnswerId, clarificationRequest);
    }

    @GetMapping("/executions/{executionId}/clarificationRequests")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ClarificationRequestDto> getClarificationRequests(Principal principal, @PathVariable Integer executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return questionDiscussionService.getClarificationRequests(user.getUsername(), executionId);
    }

    @GetMapping("/questionAnswers/{questionAnswerId}/clarificationRequests")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto getClarificationRequest(Principal principal, @PathVariable Integer questionAnswerId) {
        return questionDiscussionService.getClarification(questionAnswerId);
    }

    @PostMapping("/executions/{executionId}/clarificationRequests/{clarificationRequestId}/clarificationRequestAnswers")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationRequestAnswerDto createClarificationRequestAnswer(@PathVariable Integer executionId,
                                                                          @PathVariable Integer clarificationRequestId,
                                                                          @Valid @RequestBody ClarificationRequestAnswerDto clarificationRequestAnswerDto) {
        return questionDiscussionService.createClarificationRequestAnswer(clarificationRequestId, clarificationRequestAnswerDto);
    }
}
