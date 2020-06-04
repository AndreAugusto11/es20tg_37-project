package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
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


@RestController
public class QuestionSuggestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionSuggestionController.class);

    @Autowired
    private QuestionSuggestionService questionSuggestionService;

    @Value("${figures.dir}")
    private String figuresDir;

    @PostMapping("/courses/{courseId}/questionSuggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionSuggestionDto createSuggestionQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody QuestionSuggestionDto questionSuggestionDto) {
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name());
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return questionSuggestionService.createQuestionSuggestion(user.getId(), courseId, questionSuggestionDto);
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}/accepting")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public QuestionDto acceptQuestionSuggestion(@PathVariable int questionSuggestionId) {
        return questionSuggestionService.acceptQuestionSuggestion(questionSuggestionId);
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}/rejecting")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public ResponseEntity rejectQuestionSuggestion(Principal principal, @PathVariable int questionSuggestionId, @Valid @RequestBody JustificationDto justificationDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestionId, justificationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/questionSuggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionSuggestionDto> getQuestionSuggestions(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return questionSuggestionService.getQuestionSuggestions(user.getId(), courseId);
    }

    @GetMapping("/courses/{courseId}/allQuestionSuggestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionSuggestionDto> getAllQuestionSuggestions(@PathVariable int courseId) {
        return questionSuggestionService.getAllQuestionSuggestions(courseId);
    }

    @GetMapping("/questionSuggestions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public List<QuestionSuggestionDto> getAllQuestionSuggestions() {
        return questionSuggestionService.getAllQuestionSuggestions();
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public QuestionSuggestionDto updateRejectedQuestionSuggestion
            (@PathVariable int questionSuggestionId, @Valid @RequestBody QuestionSuggestionDto questionSuggestionDto) {
        return questionSuggestionService.updateRejectedQuestionSuggestion(questionSuggestionId, questionSuggestionDto);
    }

    @PutMapping("/questionSuggestions/{questionSuggestionId}/image")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionSuggestionId, 'QUESTION_SUGGESTION.ACCESS')")
    public String uploadImageToQuestionSuggestion(@PathVariable int questionSuggestionId, @RequestParam("file") MultipartFile file) throws IOException {
        logger.debug("uploadImage  questionId: {}: , filename: {}", questionSuggestionId, file.getContentType());
        System.out.println("uploadImage  questionId:" + questionSuggestionId + ": , filename: " + file.getContentType());
        QuestionDto questionDto = questionSuggestionService.findQuestionSuggestionById(questionSuggestionId).getQuestionDto();
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        questionSuggestionService.uploadImage(questionSuggestionId, type);

        url = questionSuggestionService.findQuestionSuggestionById(questionSuggestionId).getImage().getUrl();
        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

        return url;
    }

    @DeleteMapping("/questionSuggestions/{questionSuggestionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN'))")
    public ResponseEntity removeQuestionSuggestion(@PathVariable Integer questionSuggestionId) {
        questionSuggestionService.removeQuestionSuggestion(questionSuggestionId);

        return ResponseEntity.ok().build();
    }

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }
}
