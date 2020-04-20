package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetEnrolledTournamentsTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user
    def user2

    def setup() {
        user = new User("Manel1", "Man12", 1, User.Role.STUDENT)
        userRepository.save(user)
        user2 = new User("Manel12", "Man123", 2, User.Role.STUDENT)
        userRepository.save(user2)
    }

    def "Lists tournaments when there are 2 tournaments"() {
        given:
        def tournament1 = new Tournament(user)
        tournamentRepository.save(tournament1)
        def tournament2 = new Tournament(user)
        tournamentRepository.save(tournament2)
        user2.addTournament(tournament1)
        user2.addTournament(tournament2)
        when:
        def result = tournamentService.getEnrolledTournaments(user2.getId())
        then: "inserted data is correct"
        result.size() == 2
    }


    @TestConfiguration
    static class TournamentServiceListTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}