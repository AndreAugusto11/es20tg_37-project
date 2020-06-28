package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_TOURNAMENT_MISMATCH
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NULL_QUIZ
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_RESULTS_UNAVAILABLE

@DataJpaTest
class GetTournamentResultsTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"
    public static final String OPTION_CONTENT = "Option Content"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final LocalDateTime TOURNAMENT_AVAILABLE_DATE = DateHandler.now().minusDays(1)
    public static final LocalDateTime TOURNAMENT_RESULTS_DATE = DateHandler.now()
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_CREATION_DATE = DateHandler.now().minusMinutes(10)
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_DATE = TOURNAMENT_QUIZ_ANSWER_CREATION_DATE.plusMinutes(5)

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
    def enroller = new User()
    def quiz = new Quiz()
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

        quiz.setTitle(TOURNAMENT_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        quiz.setResultsDate(TOURNAMENT_RESULTS_DATE)

        def option1 = new Option()
        option1.setContent(OPTION_CONTENT)
        option1.setCorrect(false)
        option1.setSequence(0)
        optionRepository.save(option1)

        def option2 = new Option()
        option2.setContent(OPTION_CONTENT)
        option2.setCorrect(true)
        option2.setSequence(1)
        optionRepository.save(option2)

        def quizAnswer1 = new QuizAnswer()
        quizAnswer1.setQuiz(quiz)
        quizAnswer1.setUser(creator)
        quizAnswer1.setCreationDate(TOURNAMENT_QUIZ_ANSWER_CREATION_DATE)
        quizAnswer1.setAnswerDate(TOURNAMENT_QUIZ_ANSWER_DATE)
        quizAnswer1.setCompleted(true)

        def quizAnswer2 = new QuizAnswer()
        quizAnswer2.setQuiz(quiz)
        quizAnswer2.setUser(enroller)
        quizAnswer2.setCreationDate(TOURNAMENT_QUIZ_ANSWER_CREATION_DATE)
        quizAnswer2.setAnswerDate(TOURNAMENT_QUIZ_ANSWER_DATE.plusMinutes(2))
        quizAnswer2.setCompleted(true)

        def questionAnswer1 = new QuestionAnswer()
        questionAnswer1.setOption(option1)
        questionAnswer1.setQuizAnswer(quizAnswer1)

        def questionAnswer2 = new QuestionAnswer()
        questionAnswer2.setOption(option2)
        questionAnswer2.setQuizAnswer(quizAnswer2)

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

    def "A student gets the results of a concluded tournament"() {
        given: "A tournament quiz"
        tournament.setQuiz(quiz)

        when:
        def results = tournamentService.getTournamentResults(courseExecution.getId(), tournament.getId())

        then: "the results are returned"
        results != null
        results.size() == 2
        def result1 = results.get(0)
        def result2 = results.get(1)

        and: "The results are ordered correctly"
        result1.getEnrolledStudentUsername() == enroller.getUsername()
        result1.getNumberOfCorrectAnswers() == 1
        result1.getTimeTaken() > 0

        result2.getEnrolledStudentUsername() == creator.getUsername()
        result2.getNumberOfCorrectAnswers() == 0
        result2.getTimeTaken() > 0

        result2.getTimeTaken() < result1.getTimeTaken()
    }

    def "Cannot get tournament results given an invalid tournament id"() {
        when: "the tournaments are requested"
        tournamentService.getTournamentResults(courseExecution.getId(), 0)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Cannot get tournament results given a course execution id that does not have the specified tournament"() {
        when: "the tournaments are requested"
        tournamentService.getTournamentResults(1, tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == COURSE_EXECUTION_TOURNAMENT_MISMATCH
    }

    def "Cannot get tournament results if results launch date has not been reached yet"() {
        given: "A results date in the future"
        tournament.setResultsDate(DateHandler.now().plusDays(1))

        when:
        tournamentService.getTournamentResults(courseExecution.getId(), tournament.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_RESULTS_UNAVAILABLE
    }

    def "Cannot get tournament results if the tournament has no quiz"() {
        when:
        tournamentService.getTournamentResults(courseExecution.getId(), tournament.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NULL_QUIZ
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}