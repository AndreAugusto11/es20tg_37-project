package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
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

        if (userId == null || questionSuggestionDto == null || courseId == null) {
            throw new TutorException(INVALID_NULL_ARGUMENTS);
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
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rejectQuestionSuggestion(Integer userId, Integer questionSuggestionId, JustificationDto justificationDto) {
    }
}
