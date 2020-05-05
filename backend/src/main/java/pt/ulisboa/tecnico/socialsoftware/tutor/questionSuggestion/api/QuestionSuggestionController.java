package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
public class QuestionSuggestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionSuggestionController.class);

    @Autowired
    private QuestionSuggestionService questionSuggestionService;

    @PostMapping("/courses/{courseId}/questionSuggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionSuggestionDto createSuggestionQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody QuestionSuggestionDto questionSuggestionDto) {
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name());
        User user = (User) ((Authentication) principal).getPrincipal();
        return questionSuggestionService.createQuestionSuggestion(user.getId(), courseId, questionSuggestionDto);
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}/accepting")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public ResponseEntity acceptQuestionSuggestion(@PathVariable int questionSuggestionId) {
        questionSuggestionService.acceptQuestionSuggestion(questionSuggestionId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}/rejecting")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public ResponseEntity rejectQuestionSuggestion(Principal principal, @PathVariable int questionSuggestionId, @Valid @RequestBody JustificationDto justificationDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestionId, justificationDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/questionSuggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionSuggestionDto> getQuestionSuggestions(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return questionSuggestionService.getQuestionSuggestions(user.getId(), courseId);
    }

    @GetMapping("/courses/{courseId}/allQuestionSuggestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionSuggestionDto> getAllQuestionSuggestions(@PathVariable int courseId) {
        return questionSuggestionService.getAllQuestionSuggestions(courseId);
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public QuestionSuggestionDto updateRejectedQuestionSuggestion
            (@PathVariable int questionSuggestionId, @Valid @RequestBody QuestionSuggestionDto questionSuggestionDto) {
        return questionSuggestionService.updateRejectedQuestionSuggestion(questionSuggestionId, questionSuggestionDto);
    }
}
