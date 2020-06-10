package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StatsDto implements Serializable {
    private Integer totalQuizzes = 0;
    private Integer totalAnswers = 0;
    private Integer totalUniqueQuestions = 0;
    private float correctAnswers = 0;
    private float improvedCorrectAnswers = 0;
    private Integer uniqueCorrectAnswers = 0;
    private Integer uniqueWrongAnswers = 0;
    private Integer totalAvailableQuestions = 0;
    private Integer totalNumberCreatedTournaments = 0;
    private Integer totalNumberEnrolledTournaments = 0;
    private Integer totalClarificationRequests = 0;
    private Integer totalPublicClarificationRequests = 0;
    private Integer totalNumberSuggestions = 0;
    private Integer totalNumberSuggestionsAccepted = 0;
    private Boolean privateClarificationStats = false;
    private Boolean privateSuggestion = false;
    private Boolean privateTournamentsStats = false;

    public Integer getTotalNumberCreatedTournaments() {
        return totalNumberCreatedTournaments;
    }

    public void setTotalNumberCreatedTournaments(Integer totalNumberCreatedTournaments) {
        this.totalNumberCreatedTournaments = totalNumberCreatedTournaments;
    }

    public Integer getTotalNumberEnrolledTournaments() {
        return totalNumberEnrolledTournaments;
    }

    public void setTotalNumberEnrolledTournaments(Integer totalNumberEnrolledTournaments) {
        this.totalNumberEnrolledTournaments = totalNumberEnrolledTournaments;
    }

    public Integer getTotalNumberSuggestions() {
        return totalNumberSuggestions;
    }

    public void setTotalNumberSuggestions(Integer totalNumberSuggestions) {
        this.totalNumberSuggestions = totalNumberSuggestions;
    }

    public Integer getTotalNumberSuggestionsAccepted() {
        return totalNumberSuggestionsAccepted;
    }

    public void setTotalNumberSuggestionsAccepted(Integer totalNumberSuggestionsAccepted) {
        this.totalNumberSuggestionsAccepted = totalNumberSuggestionsAccepted;
    }

    public Integer getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(Integer totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public float getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(float correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public float getImprovedCorrectAnswers() {
        return improvedCorrectAnswers;
    }

    public void setImprovedCorrectAnswers(float improvedCorrectAnswers) {
        this.improvedCorrectAnswers = improvedCorrectAnswers;
    }

    public Integer getUniqueCorrectAnswers() {
        return uniqueCorrectAnswers;
    }

    public void setUniqueCorrectAnswers(Integer uniqueCorrectAnswers) {
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
    }

    public Integer getUniqueWrongAnswers() {
        return uniqueWrongAnswers;
    }

    public void setUniqueWrongAnswers(Integer uniqueWrongAnswers) {
        this.uniqueWrongAnswers = uniqueWrongAnswers;
    }

    public Integer getTotalAvailableQuestions() {
        return totalAvailableQuestions;
    }

    public void setTotalAvailableQuestions(Integer totalAvailableQuestions) {
        this.totalAvailableQuestions = totalAvailableQuestions;
    }

    public Integer getTotalClarificationRequests() {
        return totalClarificationRequests;
    }

    public void setTotalClarificationRequests(Integer totalClarificationRequests) {
        this.totalClarificationRequests = totalClarificationRequests;
    }

    public Integer getTotalPublicClarificationRequests() {
        return totalPublicClarificationRequests;
    }

    public void setTotalPublicClarificationRequests(Integer totalPublicClarificationRequests) {
        this.totalPublicClarificationRequests = totalPublicClarificationRequests;
    }

    public Boolean isPrivateClarificationStats() {
        return privateClarificationStats;
    }

    public void setPrivateClarificationStats(Boolean privateClarificationStats) {
        this.privateClarificationStats = privateClarificationStats;
    }

    public Boolean isPrivateSuggestion() {
        return privateSuggestion;
    }

    public void setPrivateSuggestion(Boolean privateSuggestion) {
        this.privateSuggestion = privateSuggestion;
    }

    public Boolean isPrivateTournamentsStats() { return privateTournamentsStats; }

    public void setPrivateTournamentsStats(Boolean privateTournamentsStats) {
        this.privateTournamentsStats = privateTournamentsStats;
    }

    @Override
    public String toString() {
        return "StatsDto{" +
                "totalQuizzes=" + totalQuizzes +
                ", totalAnswers=" + totalAnswers +
                ", totalUniqueQuestions=" + totalUniqueQuestions +
                ", correctAnswers=" + correctAnswers +
                ", improvedCorrectAnswers=" + improvedCorrectAnswers +
                ", uniqueCorrectAnswers=" + uniqueCorrectAnswers +
                ", uniqueWrongAnswers=" + uniqueWrongAnswers +
                '}';
    }
}
