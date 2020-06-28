export class TournamentResult {
  enrolledStudentUsername!: string;
  numberOfCorrectAnswers!: number;
  timeTaken!: number;

  constructor(jsonObj?: TournamentResult) {
    if (jsonObj) {
      this.enrolledStudentUsername = jsonObj.enrolledStudentUsername;
      this.numberOfCorrectAnswers = jsonObj.numberOfCorrectAnswers;
      this.timeTaken = jsonObj.timeTaken;
    }
  }
}