package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.FILE_NOT_DEFINED;

@RestController
public class QuestionDiscussionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionDiscussionController.class);

    @Autowired
    QuestionDiscussionService questionDiscussionService;

    @Value("${figures.dir}")
    private String figuresDir;

    @PostMapping("/questionAnswers/{questionAnswerId}/clarificationRequests")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto createClarificationRequest(@PathVariable Integer questionAnswerId,
                                                              @Valid @RequestBody ClarificationRequestDto clarificationRequest) {
        return questionDiscussionService.createClarificationRequest(questionAnswerId, clarificationRequest);
    }

    @PutMapping("/clarificationRequests/{clarificationRequestId}/uploadImage")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#clarificationRequestId, 'CLARIFICATION_REQUEST.ACCESS')")
    public String uploadClarificationRequestImage(@PathVariable Integer clarificationRequestId,
                                                                   @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null)
            throw new TutorException(FILE_NOT_DEFINED);

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        String url = questionDiscussionService.uploadImage(clarificationRequestId, type);

        Files.copy(file.getInputStream(), this.getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);
        return url;
    }

    @GetMapping("/executions/{executionId}/clarificationRequests")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ClarificationRequestDto> getClarificationRequests(Principal principal, @PathVariable Integer executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return questionDiscussionService.getClarificationRequests(user.getUsername(), executionId);
    }

    @PutMapping("/executions/{executionId}/clarificationRequests/{clarificationRequestId}/close")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationRequestDto closeClarificationRequest(@PathVariable Integer executionId,
                                                             @PathVariable Integer clarificationRequestId) {
        return questionDiscussionService.closeClarificationRequest(clarificationRequestId);
    }

    @GetMapping("/questionAnswers/{questionAnswerId}/clarificationRequests")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto getClarificationRequest(@PathVariable Integer questionAnswerId) {
        return questionDiscussionService.getClarification(questionAnswerId);
    }

    @PostMapping("/executions/{executionId}/clarificationRequests/{clarificationRequestId}/clarificationRequestAnswers")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationRequestAnswerDto createClarificationRequestAnswer(@PathVariable Integer executionId,
                                                                          @PathVariable Integer clarificationRequestId,
                                                                          @Valid @RequestBody ClarificationRequestAnswerDto clarificationRequestAnswerDto) {
        return questionDiscussionService.createClarificationRequestAnswer(clarificationRequestId, clarificationRequestAnswerDto);
    }

    @GetMapping("/executions/{executionId}/clarificationRequests/{clarificationRequestId}/clarificationRequestAnswers")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ClarificationRequestAnswerDto> getClarificationRequestAnswers(@PathVariable Integer executionId,
                                                                          @PathVariable Integer clarificationRequestId) {
        return questionDiscussionService.getClarificationRequestAnswers(clarificationRequestId);
    }

    @PostMapping("/executions/{executionId}/clarificationRequests/{clarificationRequestId}/public")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationRequestDto createPublicClarificationRequest(@PathVariable Integer executionId,
                                                                          @PathVariable Integer clarificationRequestId) {
        return questionDiscussionService.createPublicClarificationRequest(clarificationRequestId);
    }

    @PostMapping("/executions/{executionId}/clarificationRequests/{clarificationRequestId}/private")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationRequestDto removePublicClarificationRequest(@PathVariable Integer executionId,
                                                                    @PathVariable Integer clarificationRequestId) {
        return questionDiscussionService.removePublicClarificationRequest(clarificationRequestId);
    }

    @GetMapping("/executions/{executionId}/questionAnswers/{questionId}/clarificationRequests")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ClarificationRequestDto> getQuestionClarificationRequests(@PathVariable Integer executionId,
                                                                    @PathVariable Integer questionId) {
        return questionDiscussionService.getQuestionClarificationRequests(executionId, questionId);
    }

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }
}
