package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StatementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto generateStudentQuiz(int userId, int executionId, StatementCreationDto quizDetails) {

        System.out.println("\n" +
                "StatementService : generateStudentQuiz\n" +
                " - userId: " + userId + "\n" +
                " - executionId: " + executionId + "\n" +
                " - quizDetails: " + quizDetails + "\n"
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);
        quiz.setType(Quiz.QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        if (quizDetails.getAssessmentId() != null) {
            availableQuestions = filterByAssessment(availableQuestions, quizDetails);
        } else if (quizDetails.getTournamentId() != null) {
            availableQuestions = filterByTournament(availableQuestions, quizDetails, quiz);
        }
        // TODO else use default assessment

        if (availableQuestions.size() < quizDetails.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        availableQuestions = user.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generate(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);

        quizRepository.save(quiz);
        quizAnswerRepository.save(quizAnswer);

        return new StatementQuizDto(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto getQuizByQRCode(int userId, int quizId) {

        System.out.println("\n" +
                "StatementService : getQuizByQRCode\n" +
                " - userId: " + userId + "\n" +
                " - quizId: " + quizId + "\n"
        );

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (!user.getCourseExecutions().contains(quiz.getCourseExecution())) {
            throw new TutorException(USER_NOT_ENROLLED, user.getUsername());
        }

        if (quiz.getConclusionDate() != null && DateHandler.now().isAfter(quiz.getConclusionDate())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        QuizAnswer quizAnswer = quizAnswerRepository.findQuizAnswer(quiz.getId(), user.getId()).orElseGet(() -> {
            QuizAnswer qa = new QuizAnswer(user, quiz);
            quizAnswerRepository.save(qa);
            return qa;
        });

        if (quizAnswer.isCompleted()) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        }

        if (quizAnswer.getQuiz().isOneWay() && quizAnswer.getAnswerDate() != null) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        }

        if (quiz.getAvailableDate() == null || DateHandler.now().isAfter(quiz.getAvailableDate())) {
            return new StatementQuizDto(quizAnswer);

        // Send timer
        } else {
            StatementQuizDto quizDto = new StatementQuizDto();
            quizDto.setTimeToAvailability(ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getAvailableDate()));
            return quizDto;
        }
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StatementQuizDto> getAvailableQuizzes(int userId, int executionId) {

        System.out.println("\n" +
                "StatementService : getAvailableQuizzes\n" +
                " - userId: " + userId + "\n" +
                " - executionId: " + executionId + "\n"
        );

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        LocalDateTime now = DateHandler.now();

        Set<Integer> studentQuizIds =  user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getId)
                .collect(Collectors.toSet());

        // create QuizAnswer for quizzes
        quizRepository.findQuizzes(executionId).stream()
                .filter(quiz -> !quiz.isQrCodeOnly())
                .filter(quiz -> !quiz.getType().equals(Quiz.QuizType.GENERATED))
                .filter(quiz -> quiz.getAvailableDate() == null || quiz.getAvailableDate().isBefore(now))
                .filter(quiz -> !studentQuizIds.contains(quiz.getId()))
                .forEach(quiz ->  {
                    if (quiz.getConclusionDate() == null || quiz.getConclusionDate().isAfter(now)) {
                        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
                        quizAnswerRepository.save(quizAnswer);
                    }
                });

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> !quizAnswer.isCompleted())
                .filter(quizAnswer -> !quizAnswer.getQuiz().isOneWay() || quizAnswer.getCreationDate() == null)
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .filter(quizAnswer -> quizAnswer.getQuiz().getConclusionDate() == null || DateHandler.now().isBefore(quizAnswer.getQuiz().getConclusionDate()))
                .filter(quizAnswer -> quizAnswer.getQuiz().getAvailableDate().isBefore(now))
                .map(StatementQuizDto::new)
                .sorted(Comparator.comparing(StatementQuizDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto getTournamentQuiz(int userId, int tournamentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if (!user.getCourseExecutions().contains(tournament.getCourseExecution()))
            throw new TutorException(USER_NOT_ENROLLED, user.getUsername());

        if (!user.getEnrolledTournaments().contains(tournament))
            throw new TutorException(STUDENT_NOT_ENROLLED_IN_TOURNAMENT, user.getUsername());

        if (tournament.getStatus() != Tournament.Status.ONGOING)
            throw new TutorException(TOURNAMENT_NOT_ONGOING);

        if (tournament.getQuiz() == null) {
            StatementCreationDto statementCreationDto = new StatementCreationDto();
            statementCreationDto.setTournamentId(tournamentId);
            statementCreationDto.setNumberOfQuestions(tournament.getNumberOfQuestions());

            return generateStudentQuiz(userId, tournament.getCourseExecution().getId(), statementCreationDto);
        }

        QuizAnswer quizAnswer = quizAnswerRepository.findQuizAnswer(tournament.getQuiz().getId(), user.getId())
                .orElseGet(() -> {
                    QuizAnswer qa = new QuizAnswer(user, tournament.getQuiz());
                    quizAnswerRepository.save(qa);
                    return qa;
                });

        if (quizAnswer.isCompleted())
            throw new TutorException(TOURNAMENT_ALREADY_COMPLETED);

        return new StatementQuizDto(quizAnswer);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SolvedQuizDto> getSolvedQuizzes(int userId, int executionId) {

        System.out.println("\n" +
                "StatementService : getSolvedQuizzes\n" +
                " - userId: " + userId + "\n" +
                " - executionId: " + executionId + "\n"
        );

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SolvedQuizDto getSolvedQuiz(Integer userId, int executionId, int quizId) {

        System.out.println("\n" +
                "StatementService : getSolvedQuiz\n" +
                " - userId: " + userId + "\n" +
                " - executionId: " + executionId + "\n" +
                " - quizId: " + quizId + "\n"
        );

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().getId() == quizId)
                .map(SolvedQuizDto::new)
                .collect(Collectors.toList()).get(0);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SolvedQuizDto getTournamentSolvedQuiz(int userId, int executionId, int tournamentId) {

        System.out.println("\n" +
                "StatementService : getTournamentSolvedQuiz\n" +
                " - userId: " + userId + "\n" +
                " - executionId: " + executionId + "\n" +
                " - tournamentId: " + tournamentId + "\n"
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if (executionId != tournament.getCourseExecution().getId())
            throw new TutorException(COURSE_EXECUTION_TOURNAMENT_MISMATCH, executionId, tournamentId);

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuiz().getId().equals(tournament.getQuiz().getId()))
                .map(SolvedQuizDto::new)
                .collect(Collectors.toList()).get(0);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(int userId, Integer quizId) {

        System.out.println("\n" +
                "StatementService : concludeQuiz\n" +
                " - userId: " + userId + "\n" +
                " - quizId: " + quizId + "\n"
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return answerService.concludeQuiz(user, quizId);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void submitAnswer(int userId, Integer quizId, StatementAnswerDto answer) {

        System.out.println("\n" +
                "StatementService : submitAnswer\n" +
                " - userId: " + userId + "\n" +
                " - quizId: " + quizId + "\n" +
                " - answer: " + answer + "\n"
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        answerService.submitAnswer(user, quizId, answer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void completeOpenQuizAnswers() {

        System.out.println("\n" +
                "StatementService : completeOpenQuizAnswers\n"
        );

        Set<QuizAnswer> quizAnswersToClose = quizAnswerRepository.findQuizAnswersToClose(DateHandler.now());

        quizAnswersToClose.forEach(quizAnswer -> {
            if (!quizAnswer.isCompleted()) {
                quizAnswer.setAnswerDate(quizAnswer.getQuiz().getConclusionDate());
                quizAnswer.setCompleted(true);
            }

            quizAnswer.calculateStatistics();
        });
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void startQuiz(int userId, int quizId) {

        System.out.println("\n" +
                "StatementService : startQuiz\n" +
                " - userId: " + userId + "\n" +
                " - quizId: " + quizId + "\n"
        );

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (!user.getCourseExecutions().contains(quiz.getCourseExecution())) {
            throw new TutorException(USER_NOT_ENROLLED, user.getUsername());
        }

        if (quiz.getConclusionDate() != null && DateHandler.now().isAfter(quiz.getConclusionDate())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        QuizAnswer quizAnswer = quizAnswerRepository.findQuizAnswer(quizId, user.getId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, quizId));

        if (quizAnswer.isCompleted()) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        } else if (quizAnswer.getCreationDate() == null) {
            quizAnswer.setCreationDate(DateHandler.now());
        } else if (quiz.isOneWay()) {
            throw new TutorException(QUIZ_ALREADY_STARTED);
        }
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, StatementCreationDto quizDetails) {
        Assessment assessment = assessmentRepository.findById(quizDetails.getAssessmentId())
                .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, quizDetails.getAssessmentId()));

        return availableQuestions.stream()
                .filter(question -> question.belongsToAssessment(assessment))
                .collect(Collectors.toList());
    }

    public List<Question> filterByTournament(List<Question> availableQuestions, StatementCreationDto quizDetails, Quiz quiz) {
        Tournament tournament = tournamentRepository.findById(quizDetails.getTournamentId())
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, quizDetails.getTournamentId()));

        tournament.setQuiz(quiz);
        quiz.setTitle(tournament.getTitle());
        quiz.setAvailableDate(tournament.getAvailableDate());
        quiz.setConclusionDate(tournament.getConclusionDate());
        quiz.setResultsDate(tournament.getResultsDate());

        return availableQuestions.stream()
                .filter(question -> question.belongsToTournament(tournament))
                .collect(Collectors.toList());
    }
}
