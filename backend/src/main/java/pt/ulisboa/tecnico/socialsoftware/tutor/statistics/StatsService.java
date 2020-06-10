package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
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
import java.util.stream.Collectors;

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
    public StatsDto getAllStats(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

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

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());

        int totalNumberCreatedTournaments = (int) user.getCreatedTournaments().stream()
                .filter(t ->t.canResultsBePublic(executionId))
                .count();

        int totalNumberEnrolledTournaments = (int) user.getTournaments().stream()
                .filter(t ->t.canResultsBePublic(executionId))
                .count();

        int totalNumberSuggestions = (int) questionSuggestionRepository.findAll().stream().
                filter(questionSuggestion -> questionSuggestion.getUser().getId().equals(userId)).
                filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(course.getId())).
                count();

        int totalNumberSuggestionsAccepted = (int) questionSuggestionRepository.findAll().stream().
                filter(questionSuggestion -> questionSuggestion.getUser().getId().equals(userId)).
                filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(course.getId())).
                filter(questionSuggestion -> questionSuggestion.getStatus().equals(QuestionSuggestion.Status.ACCEPTED)).
                count();

        int totalClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(clarificationRequest -> clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz()
                        .getCourseExecution().getId().equals(executionId))
                .count();

        int totalPublicClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(clarificationRequest -> clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz()
                        .getCourseExecution().getId().equals(executionId) &&
                        clarificationRequest.getPublicClarificationRequest() != null)
                .count();

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        statsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        statsDto.setTotalNumberCreatedTournaments(totalNumberCreatedTournaments);
        statsDto.setTotalNumberEnrolledTournaments(totalNumberEnrolledTournaments);
        statsDto.setTotalNumberSuggestions(totalNumberSuggestions);
        statsDto.setTotalNumberSuggestionsAccepted(totalNumberSuggestionsAccepted);
        statsDto.setTotalClarificationRequests(totalClarificationRequests);
        statsDto.setTotalPublicClarificationRequests(totalPublicClarificationRequests);

        if (user.isPrivateClarificationStats() == null)
            user.setPrivateClarificationStats(false);

        if (user.isPrivateSuggestionStats() == null)
            user.setPrivateClarificationStats(false);

        if (user.isPrivateTournamentsStats() == null)
            user.setPrivateTournamentsStats(false);

        statsDto.setPrivateClarificationStats(user.isPrivateClarificationStats());
        statsDto.setPrivateSuggestionStats(user.isPrivateSuggestionStats());
        statsDto.setPrivateTournamentsStats(user.isPrivateTournamentsStats());

        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float) correctAnswers) * 100 / totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float) uniqueCorrectAnswers) * 100 / uniqueQuestions);
        }

        return statsDto;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatsDto getSimplifiedStats(int userId, int executionId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        StatsDto statsDto = new StatsDto();
        statsDto.setName(user.getName());

        int totalNumberCreatedTournaments = (int) user.getCreatedTournaments().stream()
                .filter(t ->t.canResultsBePublic(executionId))
                .count();

        int totalNumberEnrolledTournaments = (int) user.getTournaments().stream()
                .filter(t ->t.canResultsBePublic(executionId))
                .count();

        int totalNumberSuggestions = (int) questionSuggestionRepository.findAll().stream().
                filter(questionSuggestion -> questionSuggestion.getUser().getId().equals(userId)).
                filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(course.getId())).
                count();

        int totalNumberSuggestionsAccepted = (int) questionSuggestionRepository.findAll().stream().
                filter(questionSuggestion -> questionSuggestion.getUser().getId().equals(userId)).
                filter(questionSuggestion -> questionSuggestion.getCourse().getId().equals(course.getId())).
                filter(questionSuggestion -> questionSuggestion.getStatus().equals(QuestionSuggestion.Status.ACCEPTED)).
                count();

        int totalClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(clarificationRequest -> clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz()
                        .getCourseExecution().getId().equals(executionId))
                .count();

        int totalPublicClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(clarificationRequest -> clarificationRequest.getQuestionAnswer().getQuizAnswer().getQuiz()
                        .getCourseExecution().getId().equals(executionId) &&
                        clarificationRequest.getPublicClarificationRequest() != null)
                .count();

        statsDto.setTotalNumberCreatedTournaments(totalNumberCreatedTournaments);
        statsDto.setTotalNumberEnrolledTournaments(totalNumberEnrolledTournaments);
        statsDto.setTotalNumberSuggestions(totalNumberSuggestions);
        statsDto.setTotalNumberSuggestionsAccepted(totalNumberSuggestionsAccepted);
        statsDto.setTotalClarificationRequests(totalClarificationRequests);
        statsDto.setTotalPublicClarificationRequests(totalPublicClarificationRequests);

        if (user.isPrivateClarificationStats() == null)
            user.setPrivateClarificationStats(false);

        if (user.isPrivateSuggestionStats() == null)
            user.setPrivateSuggestionStats(false);

        if (user.isPrivateTournamentsStats() == null)
            user.setPrivateTournamentsStats(false);

        statsDto.setPrivateClarificationStats(user.isPrivateClarificationStats());
        statsDto.setPrivateSuggestionStats(user.isPrivateSuggestionStats());
        statsDto.setPrivateTournamentsStats(user.isPrivateTournamentsStats());

        return statsDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeClarificationStatsPrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(user.isPrivateClarificationStats() == null){
            user.setPrivateClarificationStats(false);
        }

        user.setPrivateClarificationStats(!user.isPrivateClarificationStats());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeSuggestionPrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(user.isPrivateSuggestionStats() == null){
            user.setPrivateSuggestionStats(false);
        }

        user.setPrivateSuggestionStats(!user.isPrivateSuggestionStats());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeTournamentsStatsPrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(user.isPrivateTournamentsStats() == null){
            user.setPrivateTournamentsStats(false);
        }

        user.setPrivateTournamentsStats(!user.isPrivateTournamentsStats());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StatsDto> getSimplifiedStudentsStats(int executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        return courseExecution.getUsers().stream()
                .filter(user -> user.getRole() == User.Role.STUDENT)
                .map(user -> getSimplifiedStats(user.getId(), executionId))
                .sorted(Comparator.comparing(StatsDto::getName))
                .collect(Collectors.toList());
    }
}
