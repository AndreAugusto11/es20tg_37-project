package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

class CreateQuestionSuggestionTest extends Specification {

	def tournamentService

	def setup()
	{
		tournamentService = new TournamentService()
	}

	def "create a tournament with a student, topic, number of questions and timestamps"()
	{
		// create tournament with a student, topic, number of questions and timestamps
		expect: false
	}

	def "create a tournament with a student, 3 topics, number of questions and timestamps"()
	{
		// create tournament with a student, 3 topics, number of questions and timestamps
		expect: false
	}

	def "user is empty"()
	{
		// an exception should be thrown
		expect: false
	}

	def "topic is empty"()
	{
		// an exception should be thrown
		expect: false
	}

	def "number_of_questions is empty"()
	{
		// an exception should be thrown
		expect: false
	}

	def "startTime is empty"()
	{
		// an exception should be thrown
		expect: false
	}

	def "endTime is empty"()
	{
		// an exception should be thrown
		expect: false
	}

	def "user is invalid"()
	{
		// an exception should be thrown
		expect: false
	}

	def "number_of_questions is invalid"()
	{
		// an exception should be thrown
		expect: false
	}

	def "startTime is equal to endTime"()
	{
		// an exception should be thrown
		expect: false
	}

	def "startTime is after endTime"()
	{
		// an exception should be thrown
		expect: false
	}

}