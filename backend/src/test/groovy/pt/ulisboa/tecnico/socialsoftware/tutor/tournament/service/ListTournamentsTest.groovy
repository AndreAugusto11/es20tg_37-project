package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class ListTournamentsTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user
    def formatter

    def setup() {
        user = new User("Manel1", "Man12", 1, User.Role.STUDENT)
        userRepository.save(user)

        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    }

    def "Lists tournaments when there are 2 tournaments"() {
        given:
        def tournament1 = new Tournament()
        tournament1.setCreator(user)
        tournament1.setStatus(Tournament.Status.CREATED)
        def startTime1 = LocalDateTime.now().plusDays(1)
        //startTime1.format(formatter)
        tournament1.setStartTime(startTime1)
        tournamentRepository.save(tournament1)
        def tournament2 = new Tournament()
        tournament2.setCreator(user)
        tournament2.setStatus(Tournament.Status.CREATED)
        def startTime2 = LocalDateTime.now().plusDays(1)
        //startTime2.format(formatter)
        tournament1.setStartTime(startTime2)
        tournamentRepository.save(tournament2)

        when:
        def result = tournamentService.getTournaments()

        then: "inserted data is correct"
        result.size() == 2
        and: "is sorted correctly"
        result.get(0).getId() == tournament1.getId()
        result.get(1).getId() == tournament2.getId()
    }

    def "Lists tournaments when there are no tournaments"() {
        when:
        def result = tournamentService.getTournaments()
        then:
        result.size() == 0
    }

    @TestConfiguration
    static class TournamentServiceListTestContextConfiguration {

        @Bean
        QuestionService QuestionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport AnswersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuizService QuizService() {
            return new QuizService()
        }

        @Bean
        AnswerService AnswerService() {
            return new AnswerService()
        }

        @Bean
        StatementService statementService() {
            return new StatementService()
        }

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}