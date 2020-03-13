package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository

import spock.lang.Specification


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTournamentTest extends Specification {

    public static final String TOPIC_ONE = "topicOne"
    public static final String TOPIC_TWO = "topicTwo"
    public static final String TOPIC_THREE = "topicThree"
	public static final Integer	number_of_questions = 5

	@Autowired
	TournamentService tournamentService

	@Autowired
	TournamentRepository tournamentRepository

	@Autowired
	TopicRepository topicRepository

	@Autowired
	CourseRepository courseRepository

	def student
	def topic
	def topics
	def startTime
	def endTime
	def formatter

	def setup()
	{

		student = new User('student', "istStu", 1, User.Role.STUDENT)

		topic = new HashSet<Topic>()
		def tpc = new Topic()
        tpc.setName(TOPIC_ONE)
		topic.add(tpc)
        def topicTwo = new Topic()
        topicTwo.setName(TOPIC_TWO)
		def topicThree = new Topic()
        topicThree.setName(TOPIC_THREE)

		def course = new Course("LEIC", Course.Type.TECNICO)
		courseRepository.save(course)
		course = courseRepository.findByNameType("LEIC", "TECNICO").get()
		tpc.setCourse(course)
		topicTwo.setCourse(course)
		topicThree.setCourse(course)

		topics = new HashSet<Topic>()
		topics.add(tpc)
		topics.add(topicTwo)
		topics.add(topicThree)

		topicRepository.save(tpc);
		topicRepository.save(topicTwo);
		topicRepository.save(topicThree);

		formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
		startTime = LocalDateTime.now().plusDays(1)
		startTime.format(formatter)
        endTime = LocalDateTime.now().plusDays(2)
		endTime.format(formatter)

	}

	def "create a tournament with a student, topic, number of questions and timestamps"()
	{
		// create tournament with a student, topic, number of questions and timestamps
		when:
		def result = tournamentService.createTournament(student, topic, number_of_questions, startTime, endTime)

		then: "the returned data is correct"
		result.getCreatorID() == student.getKey()
		result.getNumQuests() == number_of_questions
		result.getStartTime() == startTime
		result.getEndTime() == endTime
		def retTopic = result.getTopics()
		def iter = retTopic.iterator()
		while(iter.hasNext())
		{
			iter.next().getName() == topic[i].getName()
		}
	}

	def "create a tournament with a student, 3 topics, number of questions and timestamps"()
	{
		// create tournament with a student, 3 topics, number of questions and timestamps
		when:
		def result = tournamentService.createTournament(student, topics, number_of_questions, startTime, endTime)

		then: "the tournament was created correctly"
		result.getCreatorID() == student.getKey()
		result.getNumQuests() == number_of_questions
		result.getStartTime() == startTime
		result.getEndTime() == endTime
		def retTopic = result.getTopics()
		def iter = retTopic.iterator()
		while(iter.hasNext())
		{
			iter.next().getName() == topics[i].getName()
		}
	}

	def "save a created tournament"()
	{
		// the tournament should be saved within the database
		given: "a tournament"
		def tournament = new Tournament(student)

		when:
		tournamentRepository.save(tournament)
		def res = tournamentService.findTournamentById(tournament.getId())

		then:
		res.getId() == tournament.getId()
	}

	def "user is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(null, topic, number_of_questions, startTime, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_USER
	}

	def "topic is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, null, number_of_questions, startTime, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_TOPIC
	}

	def "number_of_questions is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topic, null, startTime, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_NUM_QUESTS
	}

	def "startTime is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topic, number_of_questions, null, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_STARTTIME
	}

	def "endTime is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topic, number_of_questions, startTime, null)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_ENDTIME
	}

	def "user is invalid"()
	{
		// an exception should be thrown
		given: "a teacher"
		def teacher = new User('teacher', "teach", 2, User.Role.TEACHER)

		when:
		def result = tournamentService.createTournament(teacher, topic, number_of_questions, startTime, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NON_VALID_USER
	}

	def "number_of_questions is invalid"()
	{
		// an exception should be thrown
		given: "a non-positive number"
		def num = 0

		when:
		def result = tournamentService.createTournament(student, topic, num, startTime, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_NUM_QUESTS
	}

	def "topics do not exist"()
	{
		// an exception should be thrown
		given: "a non-saved topic"
		def topicNS = new Topic();
		topicNS.setName("Topic Not Saved")
		def course = new Course("LEIC-A", Course.Type.TECNICO)
		courseRepository.save(course)
		course = courseRepository.findByNameType("LEIC-A", "TECNICO").get()
		topicNS.setCourse(course)
		def topicSet = new HashSet<Topic>()
		topicSet.add(topicNS)

		when:
		def result = tournamentService.createTournament(student, topicSet, number_of_questions, startTime, endTime)

		then:
		def exception = thrown(TutorException)
		exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_TOPIC
	}

	def "startTime is invalid"()
	{
		// an exception should be thrown
		given: "an a startTime before the current timeStamp"
		def format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
		def start = LocalDateTime.now().plusDays(-1)
		start.format(format)

		when:
		def result = tournamentService.createTournament(student, topic, number_of_questions, start, endTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_STARTTIME
	}

	def "startTime is equal to endTime"()
	{
		// an exception should be thrown

		when:
		def result = tournamentService.createTournament(student, topic, number_of_questions, startTime, startTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_TIMEFRAME
	}

	def "startTime is after endTime"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topic, number_of_questions, endTime, startTime)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_TIMEFRAME
	}

	@TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}