package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NON_CREATOR

@DataJpaTest
class CancelTournamentTest extends Specification{

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user
    def tournament
    def userID
    def tournamentId

    def setup()
    {
        user = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(user)
        tournament = new Tournament(user)
        tournamentRepository.save(tournament)
        user.addCreatedTournament(tournament)
        userRepository.save(user)
        user = userRepository.findByKey(1)
        tournament = tournamentService.getCreatedTournaments(user.getId()).get(0)
        userID = user.getId()
        tournamentId = tournament.getid()
    }

    def "cancel a tournament previously created"()
    {
        when:
        tournamentService.cancelTournament(userID, tournamentId)
        then: "the tournament is removed"
        tournamentRepository.count() == 0L
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
