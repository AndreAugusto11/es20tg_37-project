package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NULL_ARGUMENTS_ANSWER_DTO
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.OPTION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NO_LONGER_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_USER_MISMATCH
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class AnswerTournamentQuizTest extends Specification {

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
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_CREATION_DATE = DateHandler.now()

    @Autowired
    StatementService statementService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
    def quiz = new Quiz()
    def quizAnswer = new QuizAnswer()
    def questionAnswer = new QuestionAnswer()
    def question = new Question()
    def option = new Option()
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

        question.setKey(1)
        question.setCourse(course)
        question.setContent(QUESTION_CONTENT)
        question.setTitle(QUESTION_TITLE)
        questionRepository.save(question)

        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestion(question)
        optionRepository.save(option)

        quiz.setKey(1)
        quiz.setTitle(TOURNAMENT_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)

        quizAnswer.setQuiz(quiz)
        quizAnswer.setCreationDate(TOURNAMENT_QUIZ_ANSWER_CREATION_DATE)
        quizAnswer.setCompleted(false)

        questionAnswer.setSequence(0)
        questionAnswer.setQuizQuestion(quizQuestion)

        tournament.setTitle(TOURNAMENT_TITLE)
        tournament.setCourseExecution(courseExecution)
        tournament.setCreator(creator)
        tournament.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        tournament.setStatus(Tournament.Status.CONCLUDED)
        tournament.setQuiz(quiz)
        tournamentRepository.save(tournament)
    }

    def "A student submits an answer to a tournament quiz"() {
        given: "A student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)
        answer.setOptionId(option.getId())
        answer.setTimeTaken(1000)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "The answer is registered"
        questionAnswer.getOption().getId() == option.getId()
        questionAnswer.getTimeTaken() == 1000
        quizAnswer.getAnswerDate() != null
    }

    def "Cannot answer to a tournament quiz given an invalid user id"() {
        given: "A student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(0, tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    def "Cannot answer to a tournament quiz given an invalid quiz id"() {
        given: "A student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), 0, answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_NOT_FOUND
    }

    def "Cannot answer to a tournament quiz if the user does not have a quiz answer associated with that quiz"() {
        given: "A student answer"
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_NOT_FOUND
    }

    def "Cannot answer to a tournament quiz given no answer dto"() {
        given: "An empty student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), null)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_ANSWER_DTO
    }

    def "Cannot answer to a tournament quiz given an answer whose sequence does not match any quiz question"() {
        given: "A student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)
        answer.setSequence(2)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUESTION_ANSWER_NOT_FOUND
    }

    def "Cannot answer to a tournament quiz given a quiz answer that does not have any question answers"() {
        given: "A student answer"
        quizAnswer.setUser(creator)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUESTION_ANSWER_NOT_FOUND
    }

    def "Cannot answer to a tournament quiz given an answer that does not correspond to the student answering"() {
        given: "A different user"
        def enroller = new User()
        enroller.setName(ENROLLER_NAME)
        enroller.setUsername(ENROLLER_USERNAME)
        enroller.setKey(2)
        enroller.setRole(User.Role.STUDENT)
        enroller.addCourseExecutions(courseExecution)
        userRepository.save(enroller)

        and: "An empty student answer"
        creator.addQuizAnswer(quizAnswer)
        quizAnswer.setUser(enroller)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_USER_MISMATCH
    }

    def "Cannot answer to a tournament quiz if the time for submissions has expired"() {
        given: "A student answer"
        quiz.setConclusionDate(DateHandler.now().minusHours(1))
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_NO_LONGER_AVAILABLE
    }

    def "Cannot answer to a tournament quiz if the time for submissions has not yet open"() {
        given: "A student answer"
        quiz.setAvailableDate(DateHandler.now().plusHours(1))
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_NOT_YET_AVAILABLE
    }

    def "Cannot answer to a tournament quiz if the quiz answer option does not exist"() {
        given: "A student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)
        answer.setOptionId(0)

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == OPTION_NOT_FOUND
    }

    def "Cannot answer to a tournament quiz if the quiz answer option does not correspond to the question answered"() {
        given: "A different option"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        option.setSequence(0)
        optionRepository.save(option)

        and: "A student answer"
        quizAnswer.setUser(creator)
        questionAnswer.setQuizAnswer(quizAnswer)
        def answer = new StatementAnswerDto(questionAnswer)
        answer.setOptionId(option.getId())

        when: "The submission is made"
        statementService.submitAnswer(creator.getId(), tournament.getQuiz().getId(), answer)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUESTION_OPTION_MISMATCH
    }

    @TestConfiguration
    static class TournamentServiceSubmitTestContextConfiguration {
        @Bean
        QuestionService QuestionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport AnswersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuizService QuizService() {
            return new QuizService()
        }

        @Bean
        AnswerService AnswerService() {
            return new AnswerService()
        }

        @Bean
        StatementService statementService() {
            return new StatementService()
        }

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}