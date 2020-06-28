package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_ALREADY_ENROLLED_IN_TOURNAMENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_ENROLLMENTS_NO_LONGER_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_ENROLLED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_STUDENT

@DataJpaTest
class EnrollInTournamentTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String COURSE_ACRONYM_TWO = "SA2"
    public static final String ACADEMIC_TERM_TWO = "Second Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"
    public static final String USER_NAME = "User Name"
    public static final String USER_USERNAME = "User Username"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final String AVAILABLE_DATE = "2020-01-25T16:30:11Z"
    public static final String CONCLUSION_DATE = "2020-01-25T17:40:11Z"

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

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
        tournament.setAvailableDate(DateHandler.toLocalDateTime(AVAILABLE_DATE))
        tournament.setConclusionDate(DateHandler.toLocalDateTime(CONCLUSION_DATE))
        tournament.setStatus(Tournament.Status.ENROLLING)
        tournamentRepository.save(tournament)
    }

    def "A student enrolls in a tournament that is enrolling"() {
        when: "a student tries to enroll"
        tournamentService.enrollInTournament(enroller.getId(), tournament.getId())

        then: "the student is enrolled"
        tournamentRepository.count() == 1L
        def resultTournament = tournamentRepository.findAll().get(0)
        resultTournament != null
        resultTournament.getStatus() == Tournament.Status.ENROLLING
        resultTournament.getEnrolledUsers() != null
        resultTournament.getEnrolledUsers().size() == 2
        enroller.getEnrolledTournaments() != null
        enroller.getEnrolledTournaments().size() == 1
    }

    def "Cannot enroll in a tournament given invalid user id"() {
        when: "a student tries to enroll"
        tournamentService.enrollInTournament(0, tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    def "Cannot enroll in a tournament given invalid tournament id"() {
        when: "a student tries to enroll"
        tournamentService.enrollInTournament(enroller.getId(), 0)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Cannot enroll in a tournament if the user is not a student"() {
        given: "a teacher"
        def teacher = new User()
        teacher.setName(USER_NAME)
        teacher.setUsername(USER_USERNAME)
        teacher.setKey(3)
        teacher.setRole(User.Role.TEACHER)
        teacher.addCourseExecutions(courseExecution)
        userRepository.save(teacher)

        when: "a teacher tries to enroll"
        tournamentService.enrollInTournament(teacher.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_STUDENT
    }

    def "Cannot enroll in a tournament from a different course execution"() {
        given: "a course execution"
        def newCourseExecution = new CourseExecution()
        newCourseExecution.setCourse(course)
        newCourseExecution.setType(Course.Type.TECNICO)
        newCourseExecution.setAcronym(COURSE_ACRONYM_TWO)
        newCourseExecution.setAcademicTerm(ACADEMIC_TERM_TWO)
        courseExecutionRepository.save(newCourseExecution)

        and: "a student from a different course execution"
        def student = new User()
        student.setName(USER_NAME)
        student.setUsername(USER_USERNAME)
        student.setKey(3)
        student.setRole(User.Role.STUDENT)
        student.addCourseExecutions(newCourseExecution)
        userRepository.save(student)

        when: "a student tries to enroll"
        tournamentService.enrollInTournament(student.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_ENROLLED
    }

    @Unroll
    def "Cannot enroll in a tournament that is #test"() {
        given: "a tournament that is #test"
        tournament.setStatus(status)

        when: "a student tries to enroll"
        tournamentService.enrollInTournament(enroller.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        test        | status                      || errorMessage
        "ongoing"   | Tournament.Status.ONGOING   || TOURNAMENT_ENROLLMENTS_NO_LONGER_AVAILABLE
        "concluded" | Tournament.Status.CONCLUDED || TOURNAMENT_ENROLLMENTS_NO_LONGER_AVAILABLE
        "cancelled" | Tournament.Status.CANCELLED || TOURNAMENT_ENROLLMENTS_NO_LONGER_AVAILABLE
    }

    def "Cannot enroll in the same tournament twice"() {
        given: "a tournament with the enrolled user"
        tournament.addEnrolledUser(enroller)

        when: "a student tries to enroll"
        tournamentService.enrollInTournament(enroller.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == STUDENT_ALREADY_ENROLLED_IN_TOURNAMENT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}