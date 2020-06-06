package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import java.io.Serializable;

public class StatementCreationDto implements Serializable {
    private Integer numberOfQuestions;
    private Integer assessmentId;
    private Integer tournamentId;

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Integer assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public String toString() {
        return "StatementCreationDto{" +
                "numberOfQuestions=" + numberOfQuestions +
                ", assessment=" + assessmentId +
                ", tournament=" + tournamentId +
                '}';
    }
}
