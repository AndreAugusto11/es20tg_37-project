package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(cascade = CascadeType.ALL)
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
