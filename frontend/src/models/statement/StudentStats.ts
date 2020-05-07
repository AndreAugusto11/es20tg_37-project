export default class StudentStats {
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;
  totalNumberSuggestions!: number;
  totalNumberSuggestionsAvailable!: number;
  totalNumberCreatedTournaments!: number;
  totalNumberEnrolledTournaments!: number;
  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
      this.totalNumberSuggestions = jsonObj.totalNumberSuggestions;
      this.totalNumberSuggestionsAvailable = jsonObj.totalNumberSuggestionsAvailable;
      this.totalNumberCreatedTournaments= jsonObj.totalNumberCreatedTournaments;
      this.totalNumberEnrolledTournaments = jsonObj.totalNumberEnrolledTournaments;
    }
  }
}
