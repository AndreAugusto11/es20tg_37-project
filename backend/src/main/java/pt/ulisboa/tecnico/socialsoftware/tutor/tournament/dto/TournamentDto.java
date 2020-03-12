package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
public class TournamentDto implements Serializable {
    private String status;
    public TournamentDto() {

    }

    public TournamentDto(Tournament tournament) {

    }

    public String getStatus() { return this.status;}

    public void setStatus(String status) {this.status = status;}

}
