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
    private Integer quizID = null;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Set<String> topicsName = new HashSet<>();

    private String status;

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getid();
        creatorID = tournament.getcreator().getId();
        enrolledStudentsIds.add(creatorID);
        settopicsTour(tournament.gettopics());
        numQuests = tournament.getnumQuests();
        if(tournament.getquiz() != null) quizID = tournament.getquiz().getId();
        startTime = tournament.getstartTime();
        endTime = tournament.getendTime();
        status = tournament.getstatus().name();
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

    public Integer getquizID() {return quizID;}

    public Set<Integer> gettopics() {
        return topics;
    }

    public Set<String> gettopicsName() {
        return topicsName;
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

    public void setid(Integer id) {
        this.id = id;
    }

    public void setnumQuests(int numQuests) {
        this.numQuests = numQuests;
    }

    public void setquizID(Integer quizID){this.quizID = quizID;}

    public void settopics(Set<Integer> topics)
    {
        this.topics = topics;
    }

    public void settopicsName(Set<String> topics)
    {
        this.topicsName = topics;
    }

    public void settopicsTour(Set<Topic> topics) {
        for(Topic t: topics){
            this.topics.add(t.getId());
            this.topicsName.add(""+t.getCourse().getName()+":"+t.getName());
        }
    }

    public void setstartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setendTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getstatus() { return this.status;}

    public void setstatus(Tournament.Status status) {this.status = status.name();}

}
