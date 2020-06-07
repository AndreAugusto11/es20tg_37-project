package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND

@DataJpaTest
class FindTournamentsTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"
    public static final String TOURNAMENT_TITLE = "Tournament Title"

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
    def tournament1 = new Tournament()
    def tournament2 = new Tournament()

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

        tournament1.setTitle(TOURNAMENT_TITLE)
        tournament1.setCourseExecution(courseExecution)
        tournament1.setCreator(creator)
        tournament1.setStatus(Tournament.Status.ENROLLING)
        tournament1.addEnrolledUser(creator)
        tournament1.addEnrolledUser(enroller)
        tournamentRepository.save(tournament1)

        tournament2.setTitle(TOURNAMENT_TITLE)
        tournament2.setCourseExecution(courseExecution)
        tournament2.setCreator(creator)
        tournament2.setStatus(Tournament.Status.ENROLLING)
        tournament2.addEnrolledUser(creator)
        tournamentRepository.save(tournament2)
    }

    def "A student gets all tournaments in the course execution ordered by status"() {
        given: "an ongoing tournament"
        tournament2.setStatus(Tournament.Status.ONGOING)

        when: "a student gets all the tournaments"
        def result = tournamentService.findTournaments(courseExecution.getId())

        then: "the tournaments are sent in order"
        result != null
        result.size() == 2
        result.get(0).getId() == tournament2.getId()
        result.get(1).getId() == tournament1.getId()
        result.get(1).getStatus() == Tournament.Status.ENROLLING.name()
        result.get(0).getStatus() == Tournament.Status.ONGOING.name()
    }

    def "A student gets all tournaments in the course execution ordered by available dates"() {
        given: "an ongoing tournament"
        tournament1.setAvailableDate(DateHandler.now())
        tournament2.setAvailableDate(DateHandler.now().plusMinutes(1))

        when: "a student gets all the tournaments"
        def result = tournamentService.findTournaments(courseExecution.getId())

        then: "the tournaments are sent in order"
        result != null
        result.size() == 2
        result.get(0).getId() == tournament1.getId()
        result.get(1).getId() == tournament2.getId()
    }

    def "An enrolling tournament is updated to ongoing when a student gets all tournaments"() {
        given: "A tournament ready to open"
        tournament1.setAvailableDate(DateHandler.now())
        tournament2.setAvailableDate(DateHandler.now().plusDays(1))

        when: "a student gets all the tournaments"
        def result = tournamentService.findTournaments(courseExecution.getId())

        then: "the tournaments are updated and sent"
        result != null
        result.size() == 2
        result.get(0).getId() == tournament1.getId()
        result.get(1).getId() == tournament2.getId()
        result.get(0).getStatus() == Tournament.Status.ONGOING.name()
        result.get(1).getStatus() == Tournament.Status.ENROLLING.name()
    }

    def "An ongoing tournament is updated to concluded when a student gets all tournaments"() {
        given: "A tournament ready to close"
        tournament1.setStatus(Tournament.Status.ONGOING)
        tournament1.setAvailableDate(DateHandler.now().minusDays(1))
        tournament1.setConclusionDate(DateHandler.now())

        when: "a student gets all the tournaments"
        def result = tournamentService.findTournaments(courseExecution.getId())

        then: "the tournaments are updated and sent"
        result != null
        result.size() == 2
        result.get(0).getId() == tournament2.getId()
        result.get(1).getId() == tournament1.getId()
        result.get(1).getStatus() == Tournament.Status.CONCLUDED.name()
        result.get(0).getStatus() == Tournament.Status.ENROLLING.name()
    }

    def "An enrolling tournament is updated to concluded when there are not enough enrollments"() {
        given: "A tournament ready to close"
        tournament2.setAvailableDate(DateHandler.now())

        when: "a student gets all the tournaments"
        def result = tournamentService.findTournaments(courseExecution.getId())

        then: "the tournaments are updated and sent"
        result != null
        result.size() == 2
        result.get(0).getId() == tournament1.getId()
        result.get(1).getId() == tournament2.getId()
        tournament1.getStatus() == Tournament.Status.ENROLLING
        tournament2.getStatus() == Tournament.Status.CONCLUDED
    }

    def "A cancelled tournament is not updated"() {
        given: "A tournament ready to close"
        tournament2.setStatus(Tournament.Status.CANCELLED)

        when: "a student gets all the tournaments"
        def result = tournamentService.findTournaments(courseExecution.getId())

        then: "the tournaments are updated and sent"
        result != null
        result.size() == 2
        result.get(0).getId() == tournament1.getId()
        result.get(1).getId() == tournament2.getId()
        tournament1.getStatus() == Tournament.Status.ENROLLING
        tournament2.getStatus() == Tournament.Status.CANCELLED
    }

    def "Cannot get all tournaments given an invalid course execution id"() {
        when: "the tournaments are requested"
        tournamentService.findTournaments(0)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == COURSE_EXECUTION_NOT_FOUND
    }

    def "A course execution id is returned given a valid tournament id"() {
        when: "the tournaments are requested"
        def result = tournamentService.findTournamentCourseExecution(tournament1.getId())

        then: "the id is returned successfully"
        result != null
        result.getCourseExecutionId() == courseExecution.getId()
        result.getCourseExecutionType() == courseExecution.getType()
        result.getAcademicTerm() == courseExecution.getAcademicTerm()
        result.getAcronym() == courseExecution.getAcronym()
    }

    def "Cannot get course execution of tournament given invalid tournament id"() {
        when: "the course execution id is requested"
        tournamentService.findTournamentCourseExecution(0)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    @TestConfiguration
    static class TournamentServiceListTestContextConfiguration {

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