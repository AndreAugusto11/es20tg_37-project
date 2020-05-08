package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class StatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatsDto getStats(int userId, int executionId, int courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        StatsDto statsDto = new StatsDto();

        int totalQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .count();

        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());

        int totalClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(clarificationRequest -> clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz()
                        .getCourseExecution().getId().equals(executionId))
                .count();

        int totalPublicClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(clarificationRequest -> clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz()
                        .getCourseExecution().getId().equals(executionId) &&
                        clarificationRequest.getPublicClarificationRequest() != null)
                .count();

        int totalNumberSuggestions = (int) questionSuggestionRepository.findAll().stream().
                filter(questionSuggestion -> questionSuggestion.getUser().getId().equals(userId)).
                filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(courseId)).
                count();

        int totalNumberSuggestionsAvailable = (int) questionSuggestionRepository.findAll().stream().
                filter(questionSuggestion -> questionSuggestion.getUser().getId().equals(userId)).
                filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(courseId)).
                filter(questionSuggestion -> questionSuggestion.getStatus().equals(QuestionSuggestion.Status.ACCEPTED)).
                count();

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        statsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        statsDto.setTotalNumberSuggestions(totalNumberSuggestions);
        statsDto.setTotalNumberSuggestionsAvailable(totalNumberSuggestionsAvailable);

        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float) correctAnswers) * 100 / totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float) uniqueCorrectAnswers) * 100 / uniqueQuestions);
        }
        statsDto.setTotalClarificationRequests(totalClarificationRequests);
        statsDto.setTotalPublicClarificationRequests(totalPublicClarificationRequests);

        if (user.isPrivateClarificationStats() == null) {
            user.setPrivateClarificationStats(false);
        }

        statsDto.setPrivateClarificationStats(user.isPrivateClarificationStats());
        return statsDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeClarificationStatsPrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        user.setPrivateClarificationStats(!user.isPrivateClarificationStats());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeSuggestionPrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(user.isPrivateSuggestion() == null){
            user.setPrivateSuggestion(false);
        }

        user.setPrivateSuggestion(!user.isPrivateSuggestion());
    }

}
