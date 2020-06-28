package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service.performance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification


@DataJpaTest
class CreateTournamentPerformanceTest extends Specification {

    public static final String QUESTION_TITLE = "Question Title"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String TOPIC1_NAME = "Topic 1 Name"
    public static final String TOPIC2_NAME = "Topic 2 Name"
    public static final String TOPIC3_NAME = "Topic 3 Name"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final int NUMBER_OF_QUESTIONS = 1
    public static final String AVAILABLE_DATE = "2020-01-25T16:30:11Z"
    public static final String CONCLUSION_DATE = "2020-01-25T17:40:11Z"
    public static final String RESULTS_DATE = "2020-01-25T17:50:11Z"

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

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    QuestionRepository questionRepository

    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
    def topic1 = new Topic()
    def topic2 = new Topic()
    def topic3 = new Topic()
    def question = new Question()
    def topicConjunction = new TopicConjunction()

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

        topic1.setName(TOPIC1_NAME)
        topic1.setCourse(course)
        topicRepository.save(topic1)

        topic2.setName(TOPIC2_NAME)
        topic2.setCourse(course)
        topicRepository.save(topic2)

        topic3.setName(TOPIC3_NAME)
        topic3.setCourse(course)
        topicRepository.save(topic3)

        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setType(Question.Type.NORMAL)
        question.setStatus(Question.Status.AVAILABLE)
        question.addTopic(topic1)
        question.addTopic(topic2)
        question.addTopic(topic3)
        questionRepository.save(question)

        topicConjunction.addTopic(topic1)
        topicConjunction.addTopic(topic2)
        topicConjunction.addTopic(topic3)
    }

    def "performance testing to create 10000 tournaments"() {
        given: "a topic conjunction dto list with one element"
        def topicConjunctions = new HashSet()
        topicConjunctions.add(new TopicConjunctionDto(topicConjunction))

        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setCreatorId(creator.getId())
        tournamentDto.setCreatorName(creator.getName())
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setResultsDate(RESULTS_DATE)
        tournamentDto.setStatus(Tournament.Status.ENROLLING)
        tournamentDto.setTopicConjunctions(topicConjunctions)

        when:
        1.upto(1, {
            tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)
        })

        then:
        true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
