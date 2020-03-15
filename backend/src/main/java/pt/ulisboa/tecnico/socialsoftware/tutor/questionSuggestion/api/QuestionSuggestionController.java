package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;


@RestController
public class QuestionSuggestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionSuggestionController.class);

    private QuestionSuggestionService questionSuggestionService;

    @PostMapping("/courses/{courseId}/questionSuggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionSuggestionDto createSuggestionQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody QuestionSuggestionDto questionSuggestionDto) {
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name());
        User user = (User) ((Authentication) principal).getPrincipal();

        return this.questionSuggestionService.createSuggestionQuestion(user.getId(), courseId, questionSuggestionDto);
    }
}
