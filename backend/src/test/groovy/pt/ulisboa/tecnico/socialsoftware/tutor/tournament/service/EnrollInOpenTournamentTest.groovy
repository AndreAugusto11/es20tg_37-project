package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class EnrollInOpenTournamentTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user1
    def tournament

    def setup() {
        user1 = new User("Manel1", "Man12", 1, User.Role.STUDENT)
        userRepository.save(user1)
        tournament = new Tournament(user1)
        tournamentRepository.save(tournament)
    }

    def "Null arguments in enroll in open tournament" () {
        //null arguments exception is thrown
        given: "a student"
        def user2 = new User("Manel2","MAN123",2,User.Role.STUDENT)
        userRepository.save(user2)
        tournament.setStatus(Tournament.Status.OPEN)

        when:
        tournamentService.enrollStudentInTournament(key,id)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        key           | id                  || errorMessage
        null          | 1                   || ErrorMessage.TOURNAMENT_NULL_USER
        2             | null                || ErrorMessage.TOURNAMENT_NULL_TOURNAMENT

    }

    def "student enroll in open tournament"() {
        //student enroll in open tournament
        given: "a student"
        def user2 = new User("Manel2","MAN123",2,User.Role.STUDENT)
        userRepository.save(user2)
        tournament.setStatus(Tournament.Status.OPEN)

        when:
        tournamentService.enrollStudentInTournament(user2.getKey(),tournament.getId())

        then: "Data is inserted"
        tournament.getUsers().size() == 2
        user2.getTournaments().size() == 1
        and: "Inserted data is correct"
        tournament.getStatus() == Tournament.Status.OPEN
        tournament.getUsers().stream().anyMatch({ u -> u.getKey() == user2.getKey() })
        user2.getTournaments().stream().anyMatch({ t -> t.getId() == tournament.getId() })

    }

    def "student enroll in not open tournament"() {
        //TournamentNotOpen exception is thrown
        given: "a student"
        def user2 = new User("Manel2","MAN123",2,User.Role.STUDENT)
        userRepository.save(user2)
        tournament.setStatus(Tournament.Status.ONGOING)

        when:
        tournamentService.enrollStudentInTournament(user2.getKey(),tournament.getId())

        then: "Tournament not open exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_OPEN
    }

    def "Teacher enroll in open tournament"() {
        //TeacherTournament exception is thrown
        given: "a teacher"
        def user2 = new User("Manel2","MAN123",2,User.Role.TEACHER)
        userRepository.save(user2)
        tournament.setStatus(Tournament.Status.OPEN)

        when:
        tournamentService.enrollStudentInTournament(user2.getKey(),tournament.getId())

        then: "Tournament not open exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_STUDENT
    }

    def "Student enroll in tournament with wrong tournament id"() {
        //TournamentNotFound exception is thrown
        given: "a student"
        def user2 = new User("Manel2","MAN123",2,User.Role.STUDENT)
        userRepository.save(user2)
        tournament.setStatus(Tournament.Status.OPEN)

        when:
        tournamentService.enrollStudentInTournament(user2.getKey(),3)

        then: "Tournament not open exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_FOUND
    }

    def "Student enroll twice in tournament"() {
        //AlreadyEnrolledTournament exception is thrown
        given: "a student"
        def user2 = new User("Manel2","MAN123",2,User.Role.STUDENT)
        userRepository.save(user2)
        tournament.setStatus(Tournament.Status.OPEN)
        tournamentService.enrollStudentInTournament(user2.getKey(),tournament.getId())

        when:
        tournamentService.enrollStudentInTournament(user2.getKey(),tournament.getId())

        then: "Tournament not open exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_STUDENT_ALREADY_ENROLLED
    }

    @TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}