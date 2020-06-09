package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetCreatedTournaments extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user
    def tournament

    def setup() {
        user = new User("User", "use1", 1, User.Role.STUDENT)
        userRepository.save(user)
        tournament = new Tournament(user)
        tournamentRepository.save(tournament)
        user.addCreatedTournament(tournament)
        userRepository.save(user)
    }

    def "Returns the pre-created Tournament"() {
        when:
        def result = tournamentService.getCreatedTournaments(user.getKey())
        then: "inserted data is correct"
        result.size() == 1
        def returned = result.get(0)
        returned.getcreatorID() == user.getKey()
    }

    @TestConfiguration
    static class TournamentServiceListCreatedTournamentsTestContextConfiguration
    {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}