package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.performance

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
        def tournament

        and: "10000 tournaments"
        1.upto(1, {
            tournament = new Tournament()
            tournament.setCreator(user1)
            tournamentRepository.save(tournament)
        })
        List<Tournament> tournamentList = tournamentRepository.findAll()

        when:
        1.upto(1, {
            tournamentService.enrollStudentInTournament(user2.getId(),tournamentList.pop().getId())
        })

        then:
        true
    }


    @TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {

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