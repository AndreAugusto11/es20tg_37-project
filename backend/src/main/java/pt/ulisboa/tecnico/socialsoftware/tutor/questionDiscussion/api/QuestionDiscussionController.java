package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto;

import javax.validation.Valid;

@RestController
public class QuestionDiscussionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionDiscussionController.class);

    @Autowired
    QuestionDiscussionService questionDiscussionService;

    @PostMapping("/clarificationRequests/{questionAnswerId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto createClarificationRequest(@PathVariable Integer questionAnswerId, @Valid @RequestBody ClarificationRequestDto clarificationRequest) {
        return questionDiscussionService.createClarificationRequest(questionAnswerId, clarificationRequest);
    }
}
