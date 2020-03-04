package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TounamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class EnrollInOpenTournament extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def user1
    def user2

    def setup() {
        user1 = new User('name', "username", 1, User.Role.STUDENT)
        user2 = new User('name', "username", 1, User.Role.STUDENT)
    }

    def "student enroll in open tournament"() {
        //student enroll in open tournament
        expect: false
    }

    def "student enroll in ongoing tournament"() {
        //TournamentNotOpen exception is thrown
        expect: false
    }

    def "student enroll in closed tournament"() {
        //TournamentNotOpen exception is thrown
        expect: false
    }

    def "Teacher enroll in open tournament"() {
        //TeacherTournament exception is thrown
        expect: false
    }

    def "Student enroll in tournament with wrong tournament name"() {
        //NoSuchTournament exception is thrown
        expect: false
    }

    def "Student enroll twice in tournament"() {
        //AlreadyEnrolledTournament exception is thrown
        expect: false
    }
}