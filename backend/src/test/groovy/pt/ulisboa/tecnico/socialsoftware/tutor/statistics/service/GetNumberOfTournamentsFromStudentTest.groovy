package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetNumberOfTournamentsFromStudentTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"

    public static final String TOURNAMENT_TITLE = "Tournament Title"

    @Autowired
    StatsService statsService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository


    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
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

        tournament1.setTitle(TOURNAMENT_TITLE)
        tournament1.setCourseExecution(courseExecution)
        tournament1.setCreator(creator)
        tournament1.setStatus(Tournament.Status.ENROLLING)
        tournamentRepository.save(tournament1)

        tournament2.setTitle(TOURNAMENT_TITLE)
        tournament2.setCourseExecution(courseExecution)
        tournament2.setCreator(creator)
        tournament2.setStatus(Tournament.Status.ENROLLING)
        tournamentRepository.save(tournament2)
    }

    def "Get the number of tournaments created by a student"() {
        when:
        def result = statsService.getStats(creator.getId(), courseExecution.getId())

        then: "the result should be the number of tournaments that the student created"
        result != null
        result.getTotalNumberCreatedTournaments() == 2
    }

    def "Get the number of enrolled tournaments from student"() {
        given: "A student that enrolled in two tournaments"
        def enroller = new User()
        enroller.setName(ENROLLER_NAME)
        enroller.setUsername(ENROLLER_USERNAME)
        enroller.setKey(2)
        enroller.setRole(User.Role.STUDENT)
        enroller.addCourseExecutions(courseExecution)
        userRepository.save(enroller)

        tournament1.addEnrolledUser(enroller)
        tournament2.addEnrolledUser(enroller)

        when:
        def result = statsService.getStats(enroller.getId(), courseExecution.getId())

        then: "the result should be the number of tournaments that the student enrolled in"
        result.getTotalNumberEnrolledTournaments() == 2
    }

    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}