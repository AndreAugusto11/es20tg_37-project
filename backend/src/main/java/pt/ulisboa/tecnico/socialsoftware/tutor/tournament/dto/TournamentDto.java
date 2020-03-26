package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.*;
import java.io.Serializable;
import java.time.LocalDateTime;

public class TournamentDto implements Serializable {
    private Integer id;
    private Set<Integer> enrolledStudentsIds = new HashSet<>();
    private int creatorID;
    private Set<Integer> topics = new HashSet<>();
    private int numQuests = 1;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Tournament.Status status;

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getid();
        creatorID = tournament.getcreator().getId();
        enrolledStudentsIds.add(creatorID);
        settopicsTour(tournament.gettopics());
        numQuests = tournament.getnumQuests();
        startTime = tournament.getstartTime();
        endTime = tournament.getendTime();
    }

    public int getcreatorID()
    {
        return creatorID;
    }

    public Set<Integer> getenrolledStudentsIds()
    {
        return enrolledStudentsIds;
    }

    public Integer getid() {
        return id;
    }

    public int getnumQuests() {
        return numQuests;
    }

    public Set<Integer> gettopics() {
        return topics;
    }

    public LocalDateTime getstartTime() {
        return startTime;
    }

    public LocalDateTime getendTime() {
        return endTime;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void setenrolledStudentsIds(Set<Integer> enrolledStudentsIds) {
        this.enrolledStudentsIds.addAll(enrolledStudentsIds);
    }

    private void setid(Integer id) {
        this.id = id;
    }

    public void setnumQuests(int numQuests) {
        this.numQuests = numQuests;
    }

    public void settopics(Set<Integer> topics) {this.topics = topics;}

    public void settopicsTour(Set<Topic> topics) {
        for(Topic t: topics){
            this.topics.add(t.getId());
        }
    }

    public void setstartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setendTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Tournament.Status getstatus() { return this.status;}

    public void setstatus(Tournament.Status status) {this.status = status;}

}
