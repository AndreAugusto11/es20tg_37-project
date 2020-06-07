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
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateTournamentTest extends Specification {

	public static final String QUESTION_TITLE = "Question Title"
	public static final String EXTRA_QUESTION1_TITLE = "Extra 1 Question Title"
	public static final String EXTRA_QUESTION2_TITLE = "Extra 2 Question Title"
	public static final String COURSE_NAME = "Software Architecture"
	public static final String COURSE_ACRONYM = "SA1"
	public static final String ACADEMIC_TERM = "First Semester"
	public static final String CREATOR_NAME = "Creator Name"
	public static final String CREATOR_USERNAME = "Creator Username"
	public static final String TOPIC1_NAME = "Topic 1 Name"
	public static final String TOPIC2_NAME = "Topic 2 Name"
	public static final String TOPIC3_NAME = "Topic 3 Name"
	public static final String TOPIC_NAME = "Topic Name"
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

	def "A student creates a tournament with a title, one topic conjunction, number of questions, and dates"() {
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
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "a tournament is created successfully"
		tournamentRepository.count() == 1L
		def result = tournamentRepository.findAll().get(0)
		result.getId() != null
		result.getCourseExecution() == courseExecution
		courseExecution.getTournaments() != null
		courseExecution.getTournaments().size() == 1
		result.getTitle() == TOURNAMENT_TITLE
		result.getCreator() != null
		result.getCreator().getId() == getCreator().getId()
		result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
		result.getCreationDate() != null
		result.getAvailableDate() == DateHandler.toLocalDateTime(AVAILABLE_DATE)
		result.getConclusionDate() == DateHandler.toLocalDateTime(CONCLUSION_DATE)
		result.getResultsDate() == DateHandler.toLocalDateTime(RESULTS_DATE)
		result.getStatus() == Tournament.Status.ENROLLING
		result.getEnrolledUsers() != null
		result.getEnrolledUsers().size() == 1
		creator.getEnrolledTournaments() != null
		creator.getEnrolledTournaments().size() == 1
		def resultEnrolled = result.getEnrolledUsers().find()
		resultEnrolled.getId() == creator.getId()

		and: "the topic conjunction is set successfully"
		topicConjunctionRepository.count() == 1L
		def resultTopicConjunctions = result.getTopicConjunctions()
		resultTopicConjunctions != null
		resultTopicConjunctions.size() == 1
		def resultTopicConjunction = resultTopicConjunctions.find()
		resultTopicConjunction != null
		resultTopicConjunction.getTournament() == result
		resultTopicConjunction.getTopics() != null
		resultTopicConjunction.getTopics().size() == 3
		resultTopicConjunction.getTopics().contains(topic1)
		resultTopicConjunction.getTopics().contains(topic2)
		resultTopicConjunction.getTopics().contains(topic3)

		and: "the tournament question pool has one question"
		result.getQuestions() != null
		result.getQuestions().size() == 1
		def resultQuestion = result.getQuestions().find()
		resultQuestion != null
		resultQuestion.getKey() == 1
		resultQuestion.getTopics() != null
		resultQuestion.getTopics().size() == 3
	}

	def "A student creates a tournament with three topic conjunctions"() {
		given: "an extra question to be selected"
		def extraQuestion1 = new Question()
		extraQuestion1.setKey(2)
		extraQuestion1.setTitle(EXTRA_QUESTION1_TITLE)
		extraQuestion1.setType(Question.Type.NORMAL)
		extraQuestion1.setStatus(Question.Status.AVAILABLE)
		extraQuestion1.addTopic(topic1)
		extraQuestion1.addTopic(topic3)
		questionRepository.save(extraQuestion1)

		and: "an extra question to not be selected"
		def extraQuestion2 = new Question()
		extraQuestion2.setKey(3)
		extraQuestion2.setTitle(EXTRA_QUESTION2_TITLE)
		extraQuestion2.setType(Question.Type.NORMAL)
		extraQuestion2.setStatus(Question.Status.AVAILABLE)
		extraQuestion2.addTopic(topic2)
		questionRepository.save(extraQuestion2)

		and: "two extra topic conjunctions, where one selects 0 questions"
		def extraTopicConjunction1 = new TopicConjunction()
		extraTopicConjunction1.addTopic(topic1)
		extraTopicConjunction1.addTopic(topic3)

		def extraTopicConjunction2 = new TopicConjunction()
		extraTopicConjunction2.addTopic(topic2)
		extraTopicConjunction2.addTopic(topic3)

		and: "a topic conjunction dto list with three elements"
		def topicConjunctions = new HashSet()
		topicConjunctions.add(new TopicConjunctionDto(topicConjunction))
		topicConjunctions.add(new TopicConjunctionDto(extraTopicConjunction1))
		topicConjunctions.add(new TopicConjunctionDto(extraTopicConjunction2))

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
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "a tournament is created successfully"
		tournamentRepository.count() == 1L
		def result = tournamentRepository.findAll().get(0)
		result.getId() != null
		result.getCourseExecution() == courseExecution
		courseExecution.getTournaments() != null
		courseExecution.getTournaments().size() == 1
		result.getTitle() == TOURNAMENT_TITLE
		result.getCreator() != null
		result.getCreator().getId() == getCreator().getId()
		result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
		result.getCreationDate() != null
		result.getAvailableDate() == DateHandler.toLocalDateTime(AVAILABLE_DATE)
		result.getConclusionDate() == DateHandler.toLocalDateTime(CONCLUSION_DATE)
		result.getResultsDate() == DateHandler.toLocalDateTime(RESULTS_DATE)
		result.getStatus() == Tournament.Status.ENROLLING
		result.getEnrolledUsers() != null
		result.getEnrolledUsers().size() == 1
		creator.getEnrolledTournaments() != null
		creator.getEnrolledTournaments().size() == 1
		def resultEnrolled = result.getEnrolledUsers().find()
		resultEnrolled.getId() == creator.getId()

		and: "the topic conjunction is set successfully"
		topicConjunctionRepository.count() == 3L
		def resultTopicConjunctions = result.getTopicConjunctions()
		resultTopicConjunctions != null
		resultTopicConjunctions.size() == 3
		resultTopicConjunctions.forEach({ tc -> tc.getTournament() == result })

		and: "the tournament question pool has two questions"
		result.getQuestions() != null
		result.getQuestions().size() == 2
	}

	def "Cannot create a tournament with an invalid user id"() {
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
		tournamentService.createTournament(0, courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == USER_NOT_FOUND
	}

	def "Cannot create a tournament with an invalid course execution id"() {
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
		tournamentService.createTournament(creator.getId(), 0, tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == COURSE_EXECUTION_NOT_FOUND
	}

	def "Cannot create a tournament without a tournament dto"() {
		when:
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), null)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_TOURNAMENTDTO
	}

	def "Cannot create a tournament given a topic conjunction with invalid topic ids"() {
		given: "an invalid topic"
		def invalidTopic = new Topic()
		invalidTopic.setCourse(course)
		invalidTopic.setName(TOPIC_NAME)
		topicRepository.save(invalidTopic)

		and: "a topic conjunction with the invalid topic"
		def invalidTopicConjunction = new TopicConjunction()
		invalidTopicConjunction.addTopic(topic1)
		invalidTopicConjunction.addTopic(invalidTopic)
		topicConjunctionRepository.save(invalidTopicConjunction)

		topicRepository.delete(invalidTopic)

		and: "an invalid topic conjunction dto list"
		def topicConjunctions = new HashSet()
		topicConjunctions.add(new TopicConjunctionDto(invalidTopicConjunction))

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
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOPIC_NOT_FOUND
	}

	def "Cannot create a tournament given an empty topic conjunction"() {
		given: "an empty topic conjunction dto list"
		def topicConjunctions = new HashSet()

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
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOURNAMENT_HAS_NO_TOPICS
	}

	def "Cannot create a tournament without a title"() {
		given: "a topic conjunction dto list with one element"
		def topicConjunctions = new HashSet()
		topicConjunctions.add(new TopicConjunctionDto(topicConjunction))

		and: "a tournament dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setCreatorId(creator.getId())
		tournamentDto.setCreatorName(creator.getName())
		tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
		tournamentDto.setAvailableDate(AVAILABLE_DATE)
		tournamentDto.setConclusionDate(CONCLUSION_DATE)
		tournamentDto.setResultsDate(RESULTS_DATE)
		tournamentDto.setStatus(Tournament.Status.ENROLLING)
		tournamentDto.setTopicConjunctions(topicConjunctions)

		when:
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == INVALID_TITLE_FOR_TOURNAMENT
	}

	@Unroll
	def "Cannot create a tournament given a number of questions #test"() {
		given: "a topic conjunction dto list with one element"
		def topicConjunctions = new HashSet()
		topicConjunctions.add(new TopicConjunctionDto(topicConjunction))

		and: "a tournament dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setTitle(TOURNAMENT_TITLE)
		tournamentDto.setCreatorId(creator.getId())
		tournamentDto.setCreatorName(creator.getName())
		tournamentDto.setNumberOfQuestions(num_questions)
		tournamentDto.setAvailableDate(AVAILABLE_DATE)
		tournamentDto.setConclusionDate(CONCLUSION_DATE)
		tournamentDto.setResultsDate(RESULTS_DATE)
		tournamentDto.setStatus(Tournament.Status.ENROLLING)
		tournamentDto.setTopicConjunctions(topicConjunctions)

		when:
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == errorMessage

		where:
		test                                          | num_questions || errorMessage
		"inferior to zero"                            | -1			  || INVALID_NUMBER_OF_QUESTIONS
		"equal to zero"                               | 0			  || INVALID_NUMBER_OF_QUESTIONS
		"superior to the pool of available questions" | 2 	          || NOT_ENOUGH_QUESTIONS
	}

	@Unroll
	def "Cannot create a tournament given #test"() {
		given: "a topic conjunction dto list with one element"
		def topicConjunctions = new HashSet()
		topicConjunctions.add(new TopicConjunctionDto(topicConjunction))

		and: "a tournament dto"
		def tournamentDto = new TournamentDto()
		tournamentDto.setTitle(TOURNAMENT_TITLE)
		tournamentDto.setCreatorId(creator.getId())
		tournamentDto.setCreatorName(creator.getName())
		tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
		tournamentDto.setAvailableDate(available_date)
		tournamentDto.setConclusionDate(conclusion_date)
		tournamentDto.setResultsDate(results_date)
		tournamentDto.setStatus(Tournament.Status.ENROLLING)
		tournamentDto.setTopicConjunctions(topicConjunctions)

		when:
		tournamentService.createTournament(creator.getId(), courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == errorMessage

		where:
		test                                      | available_date  | conclusion_date | results_date    || errorMessage
		"an invalid available date"               | null            | CONCLUSION_DATE | RESULTS_DATE    || INVALID_AVAILABLE_DATE_FOR_TOURNAMENT
		"an invalid conclusion date"              | AVAILABLE_DATE  | null            | RESULTS_DATE    || INVALID_CONCLUSION_DATE_FOR_TOURNAMENT
		"a conclusion date before available date" | CONCLUSION_DATE | AVAILABLE_DATE  | RESULTS_DATE    || INVALID_CONCLUSION_DATE_FOR_TOURNAMENT
		"a results date before available date"    | CONCLUSION_DATE | RESULTS_DATE    | AVAILABLE_DATE  || INVALID_RESULTS_DATE_FOR_TOURNAMENT
		"a results date before conclusion date"   | AVAILABLE_DATE  | RESULTS_DATE    | CONCLUSION_DATE || INVALID_RESULTS_DATE_FOR_TOURNAMENT
	}

    @TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {

		@Bean
		AnswerService AnswerService() {
			return new AnswerService()
		}

		@Bean
		AnswersXmlImport AnswersXmlImport() {
			return new AnswersXmlImport()
		}

		@Bean
		QuestionService QuestionService() {
			return new QuestionService()
		}

		@Bean
		QuizService QuizService() {
			return new QuizService()
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