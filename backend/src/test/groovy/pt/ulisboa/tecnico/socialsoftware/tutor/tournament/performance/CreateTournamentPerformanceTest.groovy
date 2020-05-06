package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.performance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime


@DataJpaTest
class CreateTournamentPerformanceTest extends Specification {
    public static final String TOPIC_ONE = "topicOne"
    public static final Integer number_of_questions = 10
    public static final LocalDateTime startTime = LocalDateTime.now().plusDays(1)
    public static final LocalDateTime endTime = LocalDateTime.now().plusDays(2)

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TopicRepository topicRepository

    @Shared
    User student
    @Shared
    Set<Topic> topic

    def setup()
    {
        def user = new User("Student", "stu", 1, User.Role.STUDENT)
        userRepository.save(user)
        student = userRepository.findByKey(1)

        topic = new HashSet<Topic>()
        def tpc = new Topic()
        tpc.setName(TOPIC_ONE)
        def course = new Course("LEIC", Course.Type.TECNICO)
        courseRepository.save(course)
        course = courseRepository.findByNameType("LEIC", "TECNICO").get()
        tpc.setCourse(course)
        topic.add(tpc)
        topicRepository.save(tpc)
    }

    def "performance testing to create 10000 tournaments"()
    {

        when:
        1.upto(1, {
            tournamentService.createTournament(student, topic, number_of_questions, startTime, endTime)
        })

        then:
        true
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
