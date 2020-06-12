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
class ChangeSuggestionPrivacy extends Specification {
    public static final String USERNAME_STUDENT = "username_student"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    StatsService statsService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def user_student

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user_student = new User("name", USERNAME_STUDENT, 1, User.Role.STUDENT)
        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)
        userRepository.save(user_student)
    }

    def "Student changes clarification stats' privacy"() {
        given: "public clarification stats"
        user_student.setPrivateSuggestionStats(false)

        when:
        statsService.changeSuggestionPrivacy(user_student.getId())

        then: "clarification stats are private"
        user_student.isPrivateSuggestionStats() == true;
    }

    def "check if the privacy value is correctly set"() {
        when:
        statsService.changeSuggestionPrivacy(user_student.getId())

        then: "suggestions stats are private"
        user_student.isPrivateSuggestionStats() == true;
    }


    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {
        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}