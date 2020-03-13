package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.*;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

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
        this.id = tournament.getId();
        creatorID = tournament.getCreator().getKey();
        enrolledStudentsIds.add(creatorID);
        Set<Topic> tourTopics = tournament.getTopics();
        topics.addAll(tourTopics);
        numQuests = tournament.getNumQuests();
        startTime = tournament.getStartTime();
        endTime = tournament.getEndTime();
    }

    public int getCreatorID()
    {
        return creatorID;
    }

    public Set<Integer> getEnrolledStudentsIds()
    {
        return enrolledStudentsIds;
    }

    public Integer getId() {
        return id;
    }

    public int getNumQuests() {
        return numQuests;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void setEnrolledStudentsIds(Set<Integer> enrolledStudentsIds) {
        this.enrolledStudentsIds.addAll(enrolledStudentsIds);
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public void setNumQuests(int numQuests) {
        this.numQuests = numQuests;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics.addAll(topics);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Tournament.Status getStatus() { return this.status;}

    public void setStatus(Tournament.Status status) {this.status = status;}

}
