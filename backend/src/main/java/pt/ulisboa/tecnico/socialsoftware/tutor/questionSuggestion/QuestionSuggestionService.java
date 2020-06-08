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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
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
    private QuestionRepository questionRepository;

    @Autowired
    CourseRepository courseRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionSuggestionDto createQuestionSuggestion(Integer userId, Integer courseId, QuestionSuggestionDto questionSuggestionDto){

        if (questionSuggestionDto == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTION);
        }

        if (userId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_USER_ID);
        }

        if (courseId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_COURSE_ID);
        }

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (user.getRole() != User.Role.STUDENT) {
            throw new TutorException(USER_IS_TEACHER, userId);
        }

        if (user.getCourseExecutions().stream().noneMatch(courseExecution -> courseExecution.getCourse().getId().equals(course.getId()))) {
            throw new TutorException(USER_NOT_IN_COURSE, userId);
        }

        QuestionSuggestion questionSuggestion = new QuestionSuggestion(user, course, questionSuggestionDto);
        questionSuggestionRepository.save(questionSuggestion);
        return new QuestionSuggestionDto(questionSuggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto acceptQuestionSuggestion(Integer questionSuggestionId) {

        if (questionSuggestionId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTION_ID);
        }

        QuestionSuggestion suggestion = checkForQuestionSuggestion(questionSuggestionId);
        QuestionSuggestionDto questionSuggestionDto = new QuestionSuggestionDto(suggestion);

        questionSuggestionDto.getQuestionDto().setType(Question.Type.NORMAL.name());
        questionSuggestionDto.getQuestionDto().setStatus(Question.Status.DISABLED.name());
        questionSuggestionDto.getQuestionDto().setOptions(suggestion.dupOptions());

        Question question = new Question(suggestion.getCourse(), questionSuggestionDto.getQuestionDto());
        question.setCreationDate(LocalDateTime.now());
        questionRepository.save(question);

        suggestion.setStatus(QuestionSuggestion.Status.ACCEPTED);
        return new QuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rejectQuestionSuggestion(Integer userId, Integer questionSuggestionId, JustificationDto justificationDto) {

        if (questionSuggestionId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTION_ID);
        } else if (userId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_USER_ID);
        } else if (justificationDto == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_JUSTIFICATION);
        } else if (justificationDto.getContent() == null || justificationDto.getContent().equals("   ")) {
            throw new TutorException(JUSTIFICATION_MISSING_DATA);
        }

        QuestionSuggestion suggestion = checkForQuestionSuggestion(questionSuggestionId);

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (user.getRole() == User.Role.STUDENT) {
            throw new TutorException(USER_IS_STUDENT, userId);
        }

        Justification justification = new Justification(user, suggestion, justificationDto);
        suggestion.setStatus(QuestionSuggestion.Status.REJECTED);

        this.entityManager.persist(justification);
    }

    private QuestionSuggestion checkForQuestionSuggestion(Integer questionSuggestionId) {
        QuestionSuggestion suggestion = questionSuggestionRepository.
                findById(questionSuggestionId).
                orElseThrow(() -> new TutorException(QUESTION_SUGGESTION_NOT_FOUND, questionSuggestionId));

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
    public List<QuestionSuggestionDto> getQuestionSuggestions(Integer userId, Integer courseId) {

        if (userId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_USER_ID);
        } else if (courseId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_COURSE_ID);
        }

        return questionSuggestionRepository.findQuestionSuggestions(userId).stream()
                .filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(courseId))
                .map(QuestionSuggestionDto::new)
                .sorted(Comparator.comparing(QuestionSuggestionDto::getCreationDate).reversed())
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionSuggestionDto> getAllQuestionSuggestions(Integer courseId) {

        if (courseId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_COURSE_ID);
        }

        return questionSuggestionRepository.findAll().stream()
                .filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(courseId))
                .map(QuestionSuggestionDto::new)
                .sorted(Comparator.comparing(QuestionSuggestionDto::getCreationDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuestionSuggestionCourse(Integer questionSuggestionId) {
        return questionSuggestionRepository.findById(questionSuggestionId)
                .map(QuestionSuggestion::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_SUGGESTION_NOT_FOUND, questionSuggestionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionSuggestionDto
    updateRejectedQuestionSuggestion(Integer questionSuggestionId, QuestionSuggestionDto questionSuggestionDto) {

        if (questionSuggestionId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTION_ID);
        } else if (questionSuggestionDto == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS_SUGGESTION);
        }

        QuestionSuggestion questionSuggestion = questionSuggestionRepository.findById(questionSuggestionId).
                orElseThrow(() -> new TutorException(QUESTION_SUGGESTION_NOT_FOUND, questionSuggestionId));

        if (!questionSuggestion.getStatus().equals(QuestionSuggestion.Status.REJECTED)) {
            throw new TutorException(QUESTION_SUGGESTION_NOT_REJECTED, questionSuggestionId);
        }

        questionSuggestion.update(questionSuggestionDto);
        return new QuestionSuggestionDto(questionSuggestion);
    }
}
