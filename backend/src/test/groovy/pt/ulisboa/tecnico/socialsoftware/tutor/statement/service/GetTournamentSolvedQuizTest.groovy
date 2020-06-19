package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_TOURNAMENT_MISMATCH
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class GetTournamentSolvedQuizTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final LocalDateTime TOURNAMENT_AVAILABLE_DATE = DateHandler.now().minusDays(1)
    public static final LocalDateTime TOURNAMENT_RESULTS_DATE = DateHandler.now()
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_CREATION_DATE = DateHandler.now().minusMinutes(10)
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_DATE = TOURNAMENT_QUIZ_ANSWER_CREATION_DATE.plusMinutes(5)


    @Autowired
    QuizService quizService

    @Autowired
    AnswerService answerService

    @Autowired
    StatementService statementService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    TournamentRepository tournamentRepository

    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
    def enroller = new User()
    def tournament = new Tournament()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution.setCourse(course)
        courseExecution.setType(Course.Type.TECNICO)
        courseExecution.setAcronym(COURSE_ACRONYM)
        courseExecution.setAcademicTerm(ACADEMIC_TERM)
        courseExecutionRepository.save(courseExecution)

        creator.setName(CREATOR_NAME)
        creator.setUsername(CREATOR_USERNAME)
        creator.setKey(1)
        creator.setRole(User.Role.STUDENT)
        creator.addCourseExecutions(courseExecution)
        userRepository.save(creator)

        enroller.setName(ENROLLER_NAME)
        enroller.setUsername(ENROLLER_USERNAME)
        enroller.setKey(2)
        enroller.setRole(User.Role.STUDENT)
        enroller.addCourseExecutions(courseExecution)
        userRepository.save(enroller)

        tournament.setTitle(TOURNAMENT_TITLE)
        tournament.setCourseExecution(courseExecution)
        tournament.setCreator(creator)
        tournament.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        tournament.setResultsDate(TOURNAMENT_RESULTS_DATE)
        tournament.setStatus(Tournament.Status.CONCLUDED)
        tournament.addEnrolledUser(creator)
        tournament.addEnrolledUser(enroller)
        tournamentRepository.save(tournament)
    }

    def "A student gets the quiz results of a tournament"() {
        given: "A question"
        def question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setContent(QUESTION_CONTENT)
        question.setTitle(QUESTION_TITLE)
        questionRepository.save(question)

        and: "a correct question option"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestion(question)
        optionRepository.save(option)

        and: "A quiz"
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(TOURNAMENT_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        quiz.setResultsDate(TOURNAMENT_RESULTS_DATE)

        and: "A quiz question"
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)

        and: "A quiz answer"
        def quizAnswer = new QuizAnswer()
        quizAnswer.setUser(creator)
        quizAnswer.setQuiz(quiz)
        quizAnswer.setCreationDate(TOURNAMENT_QUIZ_ANSWER_CREATION_DATE)
        quizAnswer.setAnswerDate(TOURNAMENT_QUIZ_ANSWER_DATE)
        quizAnswer.setCompleted(true)

        and: "A question answer"
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setOption(option)

        quizRepository.save(quiz)
        tournament.setQuiz(quiz)

        when: "the tournament quiz results are requested"
        def solvedQuizDto = statementService.getTournamentSolvedQuiz(creator.getId(), courseExecution.getId(), tournament.getId())

        then: "the quiz results are presented correctly"
        solvedQuizDto != null
        def statementQuizDto = solvedQuizDto.getStatementQuiz()
        statementQuizDto != null
        statementQuizDto.getQuestions().size() == 1
        statementQuizDto.getAnswers().size() == 1
        def answer = statementQuizDto.getAnswers().get(0)
        answer.getSequence() == 0
        answer.getOptionId() == option.getId()
        solvedQuizDto.getCorrectAnswers().size() == 1
        def correct = solvedQuizDto.getCorrectAnswers().get(0)
        correct.getSequence() == 0
        correct.getCorrectOptionId() == option.getId()
    }

    def "Cannot get tournament quiz results given an invalid user id"() {
        when: "the tournament quiz results are requested"
        statementService.getTournamentSolvedQuiz(0, courseExecution.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    def "Cannot get tournament quiz results given an invalid tournament id"() {
        when: "the tournament quiz results are requested"
        statementService.getTournamentSolvedQuiz(creator.getId(), courseExecution.getId(), 0)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Cannot get tournament results given a course execution id that does not have the specified tournament"() {
        when: "the tournament quiz results are requested"
        statementService.getTournamentSolvedQuiz(creator.getId(), 1, tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == COURSE_EXECUTION_TOURNAMENT_MISMATCH
    }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {
        @Bean
        StatementService statementService() {
            return new StatementService()
        }
        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
        @Bean
        QuizService quizService() {
            return new QuizService()
        }
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
