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
class ListTournamentsTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user

    def setup() {
        user = new User("Manel1", "Man12", 1, User.Role.STUDENT)
        userRepository.save(user)
    }

    def "2 tournaments"() {
        given:
        def tournament1 = new Tournament(user)
        tournamentRepository.save(tournament1)
        def tournament2 = new Tournament(user)
        tournamentRepository.save(tournament2)
        when:
        def result = tournamentService.getTournaments()
        then: "inserted data is correct"
        result.size() == 2
        and: "is sorted correctly"
        result.get(0).getId() == tournament1.getId()
        result.get(1).getId() == tournament2.getId()
    }

    def "no tournaments"() {
        when:
        def result = tournamentService.getTournaments()
        then:
        result.size() == 0
    }

    @TestConfiguration
    static class TournamentServiceListTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}