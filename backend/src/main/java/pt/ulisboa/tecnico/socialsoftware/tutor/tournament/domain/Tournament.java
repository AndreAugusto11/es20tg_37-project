package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tournament {
    private Set<User> users = new HashSet<>();
    private String status;

    public Tournament() {
    }

    public Tournament(TournamentDto tournamentDto) {

    }

    public void addUser(User user) {this.users.add(user);}

    public String getStatus() { return this.status;}

    public void setStatus(String status) {this.status = status;}

    public Set<User> getUsers() { return this.users;}
}
