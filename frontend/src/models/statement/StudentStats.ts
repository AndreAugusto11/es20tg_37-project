export default class StudentStats {
  name!: string;
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;
  totalClarificationRequests!: number;
  totalPublicClarificationRequests!: number;
  totalNumberSuggestions!: number;
  totalNumberSuggestionsAccepted!: number;
  totalNumberCreatedTournaments!: number;
  totalNumberEnrolledTournaments!: number;
  privateClarificationStats!: boolean;
  privateSuggestionStats!: boolean;
  privateTournamentsStats!: boolean;
  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
      this.totalClarificationRequests = jsonObj.totalClarificationRequests;
      this.totalPublicClarificationRequests =
        jsonObj.totalPublicClarificationRequests;
      this.totalNumberSuggestions = jsonObj.totalNumberSuggestions;
      this.totalNumberSuggestionsAccepted = jsonObj.totalNumberSuggestionsAccepted;
      this.totalNumberCreatedTournaments= jsonObj.totalNumberCreatedTournaments;
      this.totalNumberEnrolledTournaments = jsonObj.totalNumberEnrolledTournaments;
      this.privateClarificationStats = jsonObj.privateClarificationStats;
      this.privateSuggestionStats = jsonObj.privateSuggestionStats;
      this.privateTournamentsStats = jsonObj.privateTournamentsStats;
    }
  }
}
