package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
public class TournamentDto implements Serializable {
    private Integer id;
    private Set<Integer> enrolledStudentsIds = new HashSet<>();
    private int creatorID;
    private Set<Topic> topics = new HashSet<>();
    private int numQuests = 1;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Tournament.Status status;

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        User user = tournament.getCreator();
        creatorID = user.getId();
        enrolledStudentsIds.add(creatorID);
        Set<Topic> Tourtopics = tournament.getTopics();
        for (Topic topic : Tourtopics) {
            topics.add(topic);
        }
        numQuests = tournament.getNumQuests();
        startTime = tournament.getStartTime();
        endTime = tournament.getEndTime();
    }
    public Tournament.Status getStatus() { return this.status;}

    public void setStatus(Tournament.Status status) {this.status = status;}

}
