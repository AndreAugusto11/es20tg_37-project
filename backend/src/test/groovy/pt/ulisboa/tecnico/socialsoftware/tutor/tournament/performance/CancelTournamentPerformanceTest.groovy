package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.performance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime


@DataJpaTest
class CancelTournamentPerformanceTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    @Shared
    Integer maxTests

    @Shared
    User user

    def setup()
    {
        maxTests = 10000
        user = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(user)

        for (int i = 0; i < maxTests; i++)
        {
            def tournament = new Tournament(user)
            tournamentRepository.save(tournament)
            user.addCreatedTournament(tournament)
            userRepository.save(user)
            user = userRepository.findByKey(1)
        }
    }

    def "performance testing to create 10000 tournaments"()
    {

        when:
        1.upto(maxTests, {
            def userID = user.getId()
            def tournamentId = user.getCreatedTournaments().getAt(0).getid()
            tournamentService.cancelTournament(userID, tournamentId)
        })

        then:
        true
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
