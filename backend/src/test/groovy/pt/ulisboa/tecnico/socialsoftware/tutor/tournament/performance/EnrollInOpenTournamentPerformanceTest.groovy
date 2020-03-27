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
class EnrollInOpenTournamentPerformanceTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user1

    def setup() {
        user1 = new User("Manel1", "Man12", 1, User.Role.STUDENT)
        userRepository.save(user1)
    }

    def "performance testing to enroll in 10000 tournaments"(){
        given: "a user"
        def user2 = new User("Manel12", "Man123", 2, User.Role.STUDENT)
        userRepository.save(user2)

        and: "10000 tournaments"
        1.upto(10000, {
            tournamentRepository.save(new Tournament((user1)))
        })
        List<Tournament> tournamentList = tournamentRepository.findAll()

        when:
        1.upto(10000, {
            tournamentService.enrollStudentInTournament(user2.getId(),tournamentList.pop().getid())
        })

        then:
        true
    }


    @TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}