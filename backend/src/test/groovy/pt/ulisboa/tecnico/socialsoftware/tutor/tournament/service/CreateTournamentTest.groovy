package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateQuestionSuggestionTest extends Specification {

    public static final String TOPIC_ONE = "topicOne"
    public static final String TOPIC_TWO = "topicTwo"
    public static final String TOPIC_THREE = "topicThree"
	public static final int	number_of_questions = 5

	def tournamentService
	def student
	def topicdto
	def topicsdto
	def startTime
	def endTime
	def formatter

	def setup()
	{
		tournamentService = new TournamentService()

		student = new User('student', "istStu", 1, User.Role.STUDENT)

		topicDto = new TopicDto()
        topicDto.setName(TOPIC_ONE)
        def topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_TWO)
		def topicDtoThree = new TopicDto()
        topicDtoThree.setName(TOPIC_THREE)

		topicsdto = new TopicDto[3]
		topicsdto.putAt(0, topicdto)
		topicsdto.putAt(1, topicDtoTwo)
		topicsdto.putAt(2, topicDtoThree)
        
		formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
		startTime = LocalDateTime.now()
		startTime.format(formatter)
        endTime = LocalDateTime.now().plusDays(1)
		endTime.format(formatter)

	}

	def "create a tournament with a student, topic, number of questions and timestamps"()
	{
		// create tournament with a student, topic, number of questions and timestamps
		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, startTime, endTime)

		then: "the returned data is correct"
		result.getCreator().toString() == student.toString()
		result.getNumQuestions() == number_of_questions
		result.getInitialTime() == startTime
		result.getEndTime() == endTime
		def topic = result.getTopic()
		topic[0].getname() == TOPIC_ONE
	}

	def "create a tournament with a student, 3 topics, number of questions and timestamps"()
	{
		// create tournament with a student, 3 topics, number of questions and timestamps
		when:
		def result = tournamentService.createTournament(student, topicsdto, number_of_questions, startTime, endTime)

		then: "the returned data is correct"
		result.getCreator().toString() == student.toString()
		result.getNumQuestions() == number_of_questions
		result.getInitialTime() == startTime
		result.getEndTime() == endTime
		def topic = result.getTopic()
		for (i = 0; i < 3; i++)
		{
			topic[i].getname() == topicsdto[i].getname()
		}
	}

	def "user is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(null, topicdto, number_of_questions, startTime, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_USER
	}

	def "topic is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, null, number_of_questions, startTime, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_TOPIC
	}

	def "number_of_questions is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topicdto, null, startTime, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_NUM_QUESTS
	}

	def "startTime is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, null, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_STARTTIME
	}

	def "endTime is empty"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, startTime, null)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_ENDTIME
	}

	def "user is invalid"()
	{
		// an exception should be thrown
		given: "a teacher"
		def teacher = new User('teacher', "teach", 2, User.Role.TEACHER)

		when:
		def result = tournamentService.createTournament(teacher, topicdto, number_of_questions, startTime, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NON_STUDENT_USER
	}

	def "number_of_questions is invalid"()
	{
		// an exception should be thrown
		given: "a non-positive number"
		def num = 0

		when:
		def result = tournamentService.createTournament(student, topicdto, num, startTime, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_NUM_QUESTS
	}

	def "startTime is invalid"()
	{
		// an exception should be thrown
		given: "an out-of-format timeStamp"
		def format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
		def start = LocalDateTime.now()
		start.format(format)

		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, start, endTime)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_STARTTIME
	}

	def "endTime is invalid"()
	{
		// an exception should be thrown
		given: "an out-of-format timeStamp"
		def format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
		def end = LocalDateTime.now()
		end.format(format)

		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, startTime, end)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_ENDTIME
	}

	def "startTime is equal to endTime"()
	{
		// an exception should be thrown
		given: "an out-of-format timeStamp"
		def format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
		def start = LocalDateTime.now()
		start.format(format)

		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, start, start)

        then:
        def exception = thrown(TournamentException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_TIMEFRAME
	}

	def "startTime is after endTime"()
	{
		// an exception should be thrown
		when:
		def result = tournamentService.createTournament(student, topicdto, number_of_questions, endTime, startTime)

        then:
        def exception = thrown(TournamentException)
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