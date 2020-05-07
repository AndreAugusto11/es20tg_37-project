package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NON_CREATOR

@DataJpaTest
class CancelTournamentTest extends Specification{

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository

    def user
    def tournament
    def userID
    def tournamentId

    def setup()
    {
        user = new User('student', "istStu", 1, User.Role.STUDENT)
        userRepository.save(user)
        user = userRepository.findByKey(1)

        def topicOne = new Topic()
        topicOne.setName("TOPIC1")
        def topicTwo = new Topic()
        topicTwo.setName("TOPIC2")
        def topicThree = new Topic()
        topicThree.setName("TOPIC3")

        def course = new Course("LEIC", Course.Type.TECNICO)
        courseRepository.save(course)
        course = courseRepository.findByNameType("LEIC", "TECNICO").get()
        topicOne.setCourse(course)
        topicTwo.setCourse(course)
        topicThree.setCourse(course)

        def topics = new HashSet<Topic>()
        topics.add(topicOne)
        topics.add(topicTwo)
        topics.add(topicThree)

        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)
        topicRepository.save(topicThree)

        def formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        def startTime = LocalDateTime.now().plusDays(1)
        startTime.format(formatter)
        def endTime = LocalDateTime.now().plusDays(2)
        endTime.format(formatter)

        tournament = new Tournament(user, topics, 5, startTime, endTime)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getid()
        userID = user.getId()
    }

    def "cancel a tournament previously created"()
    {
        when:
        tournamentService.cancelTournament(userID, tournamentId)
        then: "the tournament is removed"
        def t = tournamentRepository.findById(tournamentId).get()
        t.getcreator().getId() == userID
        t.getstatus() == Tournament.Status.CANCELLED
    }

    def "non-valid user"()
    {
        given: "a user ID"
        def userID = 0

        when:
        tournamentService.cancelTournament(userID, tournamentId)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def "null user"()
    {
        when:
        tournamentService.cancelTournament(null, tournamentId)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_USER
    }

    def "tournament not created by the user"()
    {
        given: "a user ID"
        def newStudent = new User("name", "userN", 2, User.Role.STUDENT)
        userRepository.save(newStudent)
        def userID = userRepository.findByKey(2).getId()

        when:
        tournamentService.cancelTournament(userID, tournamentId)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NON_CREATOR
    }

    def "tournament ID non-existent"()
    {
        when:
        tournamentService.cancelTournament(userID, null)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_ID
    }

    def "tournament ID is invalid"()
    {
        given: "and invalid Tournament ID"
        def tID = 0
        when:
        tournamentService.cancelTournament(userID, tID)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_FOUND
    }


    @TestConfiguration
    static class TournamentServiceCancelTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
