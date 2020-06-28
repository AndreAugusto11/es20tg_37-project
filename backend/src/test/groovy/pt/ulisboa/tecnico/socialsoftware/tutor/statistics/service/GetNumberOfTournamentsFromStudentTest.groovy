package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GetNumberOfTournamentsFromStudentTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = "URL"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final LocalDateTime TOURNAMENT_AVAILABLE_DATE = DateHandler.now().minusDays(1)
    public static final LocalDateTime TOURNAMENT_RESULTS_DATE = DateHandler.now()
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_CREATION_DATE = DateHandler.now().minusMinutes(10)
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_DATE = TOURNAMENT_QUIZ_ANSWER_CREATION_DATE.plusMinutes(5)

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


    def course
    def courseExecution
    def user

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User("name1", "username1", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        user.setEnrolledCoursesAcronyms(ACRONYM)
        courseExecution.getUsers().add(user)
        userRepository.save(user)
    }

    def "Get the number of tournaments created by student"() {
        given:"Student created 2 tournaments"
        def tournament1 = new Tournament()
        tournament1.setTitle(TOURNAMENT_TITLE)
        tournament1.setCourseExecution(courseExecution)
        tournament1.setCreator(user)
        tournament1.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        tournament1.setResultsDate(TOURNAMENT_RESULTS_DATE)
        tournament1.setStatus(Tournament.Status.CONCLUDED)
        tournamentRepository.save(tournament1)
        def tournament2 = new Tournament()
        tournament2.setTitle(TOURNAMENT_TITLE)
        tournament2.setCourseExecution(courseExecution)
        tournament2.setCreator(user)
        tournament2.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        tournament2.setResultsDate(TOURNAMENT_RESULTS_DATE)
        tournament2.setStatus(Tournament.Status.CONCLUDED)
        user.addCreatedTournament(tournament1)
        user.addCreatedTournament(tournament2)
        when:
        def result = statsService.getAllStats(user.getId(), courseExecution.getId())

        then: "the result should be the number of tournaments that the student created"
        result.getTotalNumberCreatedTournaments() == 2;
    }

    def "Get the number of enrolled tournaments from student"() {
        given: "Student enrolled in 1 tournament"
        def user2 = new User("name12", "username12", 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution)
        user2.setEnrolledCoursesAcronyms(ACRONYM)
        courseExecution.getUsers().add(user2)
        userRepository.save(user2)
        def tournament1 = new Tournament()
        tournament1.setTitle(TOURNAMENT_TITLE)
        tournament1.setCourseExecution(courseExecution)
        tournament1.setCreator(user2)
        tournament1.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
        tournament1.setResultsDate(TOURNAMENT_RESULTS_DATE)
        tournament1.setStatus(Tournament.Status.ENROLLING)
        tournamentRepository.save(tournament1)
        user.addEnrolledTournament(tournament1)
        when: "nothing"
        def result = statsService.getAllStats(user.getId(), courseExecution.getId())

        then: "the result should be the number of suggestion accepted that the student made"
        result.getTotalNumberEnrolledTournaments() == 1;
    }

    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}