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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ChangeTournamentsPrivacy extends Specification{

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"

    @Autowired
    StatsService statsService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course = new Course()
    def courseExecution = new CourseExecution()
    def user = new User()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution.setCourse(course)
        courseExecution.setType(Course.Type.TECNICO)
        courseExecution.setAcronym(COURSE_ACRONYM)
        courseExecution.setAcademicTerm(ACADEMIC_TERM)
        courseExecutionRepository.save(courseExecution)

        user.setName(CREATOR_NAME)
        user.setUsername(CREATOR_USERNAME)
        user.setKey(1)
        user.setRole(User.Role.STUDENT)
        user.addCourseExecutions(courseExecution)
        userRepository.save(user)
    }

    def "Student changes tournament stats privacy"() {
        given: "public tournament stats"
        user.setPrivateTournamentsStats(false)

        when:
        statsService.changeTournamentsStatsPrivacy(user.getId())

        then: "tournament stats are private"
        user.isPrivateTournamentsStats()
    }

    def "check if the privacy value is correctly set"() {
        when:
        statsService.changeTournamentsStatsPrivacy(user.getId())

        then: "tournaments stats are private"
        user.isPrivateTournamentsStats() == true;
    }

    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {
        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
