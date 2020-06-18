package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;

public class TournamentResultsDto implements Serializable {
    private String enrolledStudentUsername;
    private Integer numberOfCorrectAnswers;
    private Long timeTaken;

    public TournamentResultsDto(QuizAnswer quizAnswer) {
        this.enrolledStudentUsername = quizAnswer.getUser().getUsername();
        this.numberOfCorrectAnswers = (int) quizAnswer.getQuestionAnswers().stream()
                .filter(QuestionAnswer::isCorrect)
                .count();
        this.timeTaken = ChronoUnit.SECONDS.between(quizAnswer.getCreationDate(), quizAnswer.getAnswerDate());
    }

    public String getEnrolledStudentUsername() {
        return enrolledStudentUsername;
    }

    public void setEnrolledStudentUsername(String enrolledStudentUsername) {
        this.enrolledStudentUsername = enrolledStudentUsername;
    }

    public Integer getNumberOfCorrectAnswers() {
        return numberOfCorrectAnswers;
    }

    public void setNumberOfCorrectAnswers(Integer numberOfCorrectAnswers) {
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }
}
