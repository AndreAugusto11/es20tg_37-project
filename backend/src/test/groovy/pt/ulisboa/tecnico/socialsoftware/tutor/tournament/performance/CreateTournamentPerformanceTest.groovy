package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.performance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime


@DataJpaTest
class CreateTournamentPerformanceTest extends Specification {
    static final Set<Integer> topics = [82]
    static final Integer num = 10
    static final LocalDateTime startTime = LocalDateTime.now().plusDays(1)
    static final LocalDateTime endTime = LocalDateTime.now().plusDays(2)

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    def "performance testing to get 10000 course executions"() {
        given: "a student"
        def student = new User("Student", "stu", 1, User.Role.STUDENT)
        userRepository.save(student)
        and: "a tournamentDto"
        def tournamentdto = new TournamentDto()
        tournamentdto.setCreatorID(student.getId())
        tournamentdto.settopics(topics)
        tournamentdto.setnumQuests(num)
        tournamentdto.setstartTime(startTime)
        tournamentdto.setendTime(endTime)

        when:
        1.upto(10000, {
            tournamentService.createTournament(student.getId(), tournamentdto)
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
