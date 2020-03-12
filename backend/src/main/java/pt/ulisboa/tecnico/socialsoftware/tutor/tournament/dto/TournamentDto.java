package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.io.Serializable;

public class TournamentDto implements Serializable {
    private String status;
    public TournamentDto() {

    }

    public TournamentDto(Tournament tournament) {

    }

    public String getStatus() { return this.status;}

    public void setStatus(String status) {this.status = status;}

}
