package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service.performance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime


@DataJpaTest
class CreateTournamentPerformanceTest extends Specification {

    private static final String TOPIC_ONE = "topicOne"
    private static final Integer numberQuestions = 10
    private static final String startTime = DateHandler.toISOString(LocalDateTime.now().plusDays(1))
    private static final String endTime = DateHandler.toISOString(LocalDateTime.now().plusDays(2))

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TopicRepository topicRepository

    @Shared
    def topic
    def student


    def setup() {
        def course = new Course("LEIC", Course.Type.TECNICO)
        courseRepository.save(course)
        course = courseRepository.findByNameType("LEIC", "TECNICO").get()

        def user = new User("Student", "stu", 1, User.Role.STUDENT)
        userRepository.save(user)
        student = userRepository.findByKey(1)

        topic = new Topic()
        topic.setName(TOPIC_ONE)
        topic.setCourse(course)
        topicRepository.save(topic)
    }

    def "performance testing to create 10000 tournaments"() {
        given: "a topic dto"
        def topicDto = new TopicDto(topic)
        def topicsDto = new HashSet<TopicDto>()
        topicsDto.add(topicDto)

        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setCreatorId(student.getId())
        tournamentDto.setTopics(topicsDto)
        tournamentDto.setNumberOfAvailableQuestions(numberQuestions)
        tournamentDto.setStartTime(startTime)
        tournamentDto.setEndTime(endTime)

        when:
        1.upto(1, {
            tournamentService.createTournament(student.getId(), tournamentDto)
        })

        then:
        true
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

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
