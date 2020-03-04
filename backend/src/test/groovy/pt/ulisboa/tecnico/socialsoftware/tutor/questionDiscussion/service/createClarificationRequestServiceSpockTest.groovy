package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService
import spock.lang.Specification

class createClarificationRequestServiceSpockTest extends Specification {

    def questionDiscussionService

    def setup() {
        questionDiscussionService = new QuestionDiscussionService()
    }

    def "create clarification request to answered question"() {
        // clarification request is created
        // status is open
        expect: false
    }

    def "create clarification request to non answered question"() {
        // an exception is thrown
        expect: false
    }

    def "clarification request is empty"() {
        // an exception is thrown
        expect: false
    }

    def "clarification request is blank"() {
        // an exception is thrown
        expect: false
    }

}
