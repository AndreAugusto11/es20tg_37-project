package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.*;
import java.io.Serializable;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {
    private Integer id;
    private String title;
    private Integer creatorId;
    private String creatorName;
    private Integer numberOfQuestions;
    private Integer numberOfAvailableQuestions;
    private String creationDate;
    private String availableDate;
    private String conclusionDate;
    private String resultsDate;
    private String status;
    private Set<TopicConjunctionDto> topicConjunctions = new HashSet<>();
    private Set<Integer> enrolledStudentsIds = new HashSet<>();
    private Set<String> enrolledStudentsNames = new HashSet<>();

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.title = tournament.getTitle();
        this.creatorId = tournament.getCreator().getId();
        this.creatorName = tournament.getCreator().getName();
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.numberOfAvailableQuestions = tournament.getQuestions().size();
        this.creationDate = DateHandler.toISOString(tournament.getCreationDate());
        this.availableDate = DateHandler.toISOString(tournament.getAvailableDate());
        this.conclusionDate = DateHandler.toISOString(tournament.getConclusionDate());
        this.resultsDate = DateHandler.toISOString(tournament.getResultsDate());
        this.status = tournament.getStatus().name();

        this.topicConjunctions = tournament.getTopicConjunctions().stream()
                .map(TopicConjunctionDto::new)
                .collect(Collectors.toSet());

        this.enrolledStudentsIds.addAll(tournament.getEnrolledUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList()));

        this.enrolledStudentsNames.addAll(tournament.getEnrolledUsers().stream()
                .map(User::getName)
                .collect(Collectors.toList()));
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() { return this.title; }

    public void setTitle(String title) { this.title = title; }

    public Integer getCreatorId()
    {
        return this.creatorId;
    }

    public void setCreatorId(Integer creatorID) {
        this.creatorId = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getNumberOfAvailableQuestions() {
        return this.numberOfAvailableQuestions;
    }

    public void setNumberOfAvailableQuestions(Integer numberOfAvailableQuestions) {
        this.numberOfAvailableQuestions = numberOfAvailableQuestions;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public String getResultsDate() {
        return resultsDate;
    }

    public void setResultsDate(String resultsDate) {
        this.resultsDate = resultsDate;
    }

    public String getStatus() { return this.status; }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(Tournament.Status status) { this.status = status.name(); }

    public Set<TopicConjunctionDto> getTopicConjunctions() { return topicConjunctions; }

    public void setTopicConjunctions(Set<TopicConjunctionDto> topicConjunctions) {
        this.topicConjunctions = topicConjunctions;
    }

    public void addTopicConjunction(TopicConjunctionDto topicConjunctionDto) {
        this.topicConjunctions.add(topicConjunctionDto);
    }

    public Set<Integer> getEnrolledStudentsIds() { return enrolledStudentsIds; }

    public void setEnrolledStudentsIds(Set<Integer> enrolledStudentsIds) {
        this.enrolledStudentsIds = enrolledStudentsIds;
    }

    public void addEnrolledStudentsId(Integer enrolledStudentsId) {
        this.enrolledStudentsIds.add(enrolledStudentsId);
    }

    public Set<String> getEnrolledStudentsNames() {
        return enrolledStudentsNames;
    }

    public void setEnrolledStudentsNames(Set<String> enrolledStudentsNames) {
        this.enrolledStudentsNames = enrolledStudentsNames;
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creatorId=" + creatorId +
                ", creatorName='" + creatorName + '\'' +
                ", numberOfQuestions=" + numberOfAvailableQuestions +
                ", creationDate='" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", resultsDate='" + resultsDate + '\'' +
                ", status='" + status + '\'' +
                ", topicConjunctions=" + topicConjunctions +
                ", enrolledStudentsIds=" + enrolledStudentsIds +
                ", enrolledStudentsNames=" + enrolledStudentsNames +
                '}';
    }
}
