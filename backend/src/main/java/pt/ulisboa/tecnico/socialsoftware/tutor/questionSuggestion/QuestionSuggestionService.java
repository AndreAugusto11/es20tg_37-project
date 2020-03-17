package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.Justification;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QuestionSuggestionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository;

    @Autowired
    CourseRepository courseRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionSuggestionDto createSuggestionQuestion(Integer userId, Integer courseId, QuestionSuggestionDto questionSuggestionDto){

        if (questionSuggestionDto == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTION);
        }

        if (userId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_USERID);
        }

        if (courseId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_COUSEID);
        }

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (user.getRole() != User.Role.STUDENT) {
            throw new TutorException(USER_IS_TEACHER, userId);
        }

        if (user.getCourseExecutions().stream().noneMatch(courseExecution -> courseExecution.getCourse().getId().equals(course.getId()))) {
            throw new TutorException(USER_NOT_IN_COURSE, userId);
        }

        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name());

        QuestionSuggestion questionSuggestion = new QuestionSuggestion(user, course, questionSuggestionDto);
        questionSuggestion.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(questionSuggestion);

        return new QuestionSuggestionDto(questionSuggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void acceptQuestionSuggestion(Integer questionSuggestionId) {

        if (questionSuggestionId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTIONID);
        }

        QuestionSuggestion suggestion = checkForQuestionSuggestion(questionSuggestionId);

        suggestion.setStatus(QuestionSuggestion.Status.ACCEPTED);
        suggestion.getQuestion().setStatus(Question.Status.AVAILABLE);
        suggestion.getQuestion().setCreationDate(LocalDateTime.now());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rejectQuestionSuggestion(Integer userId, Integer questionSuggestionId, JustificationDto justificationDto) {

        if (questionSuggestionId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTIONID);
        } else if (userId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_USERID);
        } else if (justificationDto == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_JUTIFICATIONDTO);
        } else if (justificationDto.getContent() == null || justificationDto.getContent().equals("   ")) {
            throw new TutorException(JUSTIFICATION_MISSING_DATA);
        }

        QuestionSuggestion suggestion = checkForQuestionSuggestion(questionSuggestionId);

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (user.getRole() == User.Role.STUDENT) {
            throw new TutorException(USER_IS_STUDENT, userId);
        }

        Justification justification = new Justification(user, suggestion, justificationDto);

        suggestion.setJustification(justification);
        suggestion.setStatus(QuestionSuggestion.Status.REJECTED);

        this.entityManager.persist(justification);
    }

    private QuestionSuggestion checkForQuestionSuggestion(Integer questionSuggestionId) {
        QuestionSuggestion suggestion = questionSuggestionRepository.findById(questionSuggestionId).orElseThrow(() -> new TutorException(QUESTION_SUGGESTION_NOT_FOUND, questionSuggestionId));

        if (suggestion.getStatus() == QuestionSuggestion.Status.ACCEPTED) {
            throw new TutorException(QUESTION_SUGGESTION_ALREADY_ACCEPTED);
        } else if (suggestion.getStatus() == QuestionSuggestion.Status.REJECTED) {
            throw new TutorException(QUESTION_SUGGESTION_ALREADY_REJECTED);
        }

        return suggestion;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionSuggestionDto> getQuestionSuggestions(int userId, Integer courseId) {
        return questionSuggestionRepository.findQuestionSuggestions(userId).stream()
                .filter(questionSuggestion -> questionSuggestion.getQuestion().getCourse().getId() == courseId)
                .map(QuestionSuggestionDto::new)
                .sorted(Comparator.comparing(QuestionSuggestionDto::getCreationDate))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuestionSuggestionCourse(Integer questionSuggestionId) {
        return questionSuggestionRepository.findById(questionSuggestionId)
                .map(QuestionSuggestion::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_SUGGESTION_NOT_FOUND, questionSuggestionId));
    }
}
