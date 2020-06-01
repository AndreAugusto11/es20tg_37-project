package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateTournamentTest extends Specification {

    private static final String TOPIC_ONE = "topicOne"
    private static final String TOPIC_TWO = "topicTwo"
    private static final String TOPIC_THREE = "topicThree"
	private static final Integer INVALID_USER_ID = 3456;
	private static final Integer NUMBER_QUESTIONS = 5

	@Autowired
	TournamentService tournamentService

	@Autowired
	TournamentRepository tournamentRepository

	@Autowired
	TopicRepository topicRepository

	@Autowired
	CourseRepository courseRepository

	@Autowired
	UserRepository userRepository

	@Shared
	def startTime

	@Shared
	def endTime

	def student
	def topicsDto
	def formatter

	def setup() {
		def course = new Course("LEIC", Course.Type.TECNICO)
		courseRepository.save(course)

		student = new User('student', "studentName", 1, User.Role.STUDENT)
		userRepository.save(student)

		def topicOne = new Topic()
        topicOne.setName(TOPIC_ONE)
		topicOne.setCourse(course)
		topicRepository.save(topicOne);

        def topicTwo = new Topic()
        topicTwo.setName(TOPIC_TWO)
		topicTwo.setCourse(course)
		topicRepository.save(topicTwo);

		def topicThree = new Topic()
        topicThree.setName(TOPIC_THREE)
		topicThree.setCourse(course)
		topicRepository.save(topicThree);

		topicsDto = new HashSet<TopicDto>()
		topicsDto.add(new TopicDto(topicOne))
		topicsDto.add(new TopicDto(topicTwo))
		topicsDto.add(new TopicDto(topicThree))

		formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

		startTime = LocalDateTime.now().plusDays(1)
		startTime.format(formatter)
        endTime = LocalDateTime.now().plusDays(2)
		endTime.format(formatter)

	}

	def "create a tournament with a student, topic, number of questions and timestamps"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(startTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

		then: "the returned data is correct"
		tournamentRepository.findAll().size() == 1
		def result = tournamentRepository.findAll().get(0)
		result.getCreator().getId() == student.getId()
		result.getTopics().size() == topicsDto.size()
		result.getNumberQuestions() == NUMBER_QUESTIONS
		result.getStartTime() == startTime
		result.getEndTime() == endTime
	}

	def "user is not defined"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(startTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(null, tournamentDto)

		then:
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOURNAMENT_NULL_USER
	}

	def "topic is empty"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(new HashSet<TopicDto>())
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(startTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

		then:
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOURNAMENT_WITH_NO_TOPICS
	}

	def "topic is not defined"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(null)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(startTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

		then:
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOURNAMENT_WITH_NULL_TOPICS
	}

	@Unroll
	def "invalid arguments: numberQuestions=#numberQuestions | startTime=#start | endTime=#end || errorMessage=#errorMessage" () {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(numberQuestions)
		tournamentDto.setStartTime(start)
		tournamentDto.setEndTime(end)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

		then:
		def exception = thrown(TutorException)
		exception.getErrorMessage() == errorMessage

		where:
		numberQuestions  |	start     | end     || errorMessage
		null             | 	startTime | endTime || TOURNAMENT_NULL_NUM_QUESTS
		NUMBER_QUESTIONS | 	null      | endTime || TOURNAMENT_NULL_STARTTIME
		NUMBER_QUESTIONS | 	startTime | null    || TOURNAMENT_NULL_ENDTIME
	}

	def "a non existing user creates a tournament"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(INVALID_USER_ID)
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(startTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(INVALID_USER_ID, tournamentDto)

		then:
		def exception = thrown(TutorException)
		exception.getErrorMessage() == USER_NOT_FOUND
	}

	def "number_of_questions is invalid"() {
		given: "a tournament Dto with a non-positive number of questions"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(0)
		tournamentDto.setStartTime(startTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_INVALID_NUM_QUESTS
	}

	def "startTime is invalid"() {
		given: "an a startTime before the current timeStamp"

		def start = LocalDateTime.now().plusDays(-1)
		start.format(formatter)

		and: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(start)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_INVALID_STARTTIME
	}

	def "startTime is equal to endTime"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(endTime)
		tournamentDto.setEndTime(endTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_INVALID_TIMEFRAME
	}

	def "startTime is after endTime"() {
		given: "a tournament Dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(student.getId())
		tournamentDto.setTopics(topicsDto)
		tournamentDto.setNumberQuestions(NUMBER_QUESTIONS)
		tournamentDto.setStartTime(endTime)
		tournamentDto.setEndTime(startTime)

		when:
		tournamentService.createTournament(student.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_INVALID_TIMEFRAME
	}

	@TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {

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