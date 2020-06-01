package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {

    private Integer id;
    private Set<Integer> enrolledStudentsIds = new HashSet<>();
    private int creatorID;
    private Set<TopicDto> topics = new HashSet<>();
    private int numberQuestions = 1;
    private String creatorName;
    private Integer quizID = null;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status;

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.creatorID = tournament.getCreator().getId();
        this.enrolledStudentsIds.add(creatorID);
        this.topics = tournament.getTopics().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toSet());
        this.numberQuestions = tournament.getNumberQuestions();
        if (tournament.getQuiz() != null)
            this.quizID = tournament.getQuiz().getId();
        this.startTime = tournament.getStartTime();
        this.endTime = tournament.getEndTime();
        this.status = tournament.getStatus().name();
        creatorName = tournament.getCreator().getName();
    }

    public int getCreatorID()
    {
        return this.creatorID;
    }

    public Set<Integer> getEnrolledStudentsIds()
    {
        return this.enrolledStudentsIds;
    }

    public Integer getId() {
        return this.id;
    }

    public int getNumberQuestions() {
        return this.numberQuestions;
    }

    public Integer getQuizID() {return this.quizID;}

    public Set<TopicDto> getTopics() {
        return this.topics;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void setEnrolledStudentsIds(Set<Integer> enrolledStudentsIds) {
        this.enrolledStudentsIds.addAll(enrolledStudentsIds);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumberQuestions(int numberQuestions) {
        this.numberQuestions = numberQuestions;
    }

    public void setQuizID(Integer quizID){this.quizID = quizID;}

    public void setTopics(Set<TopicDto> topics)
    {
        this.topics = topics;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() { return this.status;}

    public void setStatus(Tournament.Status status) {this.status = status.name();}

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
