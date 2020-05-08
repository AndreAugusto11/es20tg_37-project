import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';

export default class StatementManager {
  assessment!: number;
  numberOfQuestions: string = '5';
  statementQuiz: StatementQuiz | null = null;
  correctAnswers: StatementCorrectAnswer[] = [];

  private static _quiz: StatementManager = new StatementManager();

  static get getInstance(): StatementManager {
    return this._quiz;
  }

  async getQuizStatement() {
    let params = {
      assessment: this.assessment,
      numberOfQuestions: +this.numberOfQuestions
    };

    this.statementQuiz = await RemoteServices.generateStatementQuiz(params);
  }

  async getTournamentQuiz(quizId:number) {
    let quiz = await RemoteServices.getAvailableQuizzes()
                .then(res => res.find(q=>q.id == quizId));
    if(quiz != null) this.statementQuiz = quiz;
    else {
      throw Error('No quiz');
    }
  }

  async concludeQuiz() {
    if (this.statementQuiz) {
      let answers;
      if(this.statementQuiz.tournamentID == null) {
        answers = await RemoteServices.concludeQuiz(this.statementQuiz.id);
      }
      else{
        answers = await RemoteServices.concludeTournamentQuiz(this.statementQuiz.tournamentID);
      }
      if (answers) {
        this.correctAnswers = answers;
      }
    } else {
      throw Error('No quiz');
    }
  }

  reset() {
    this.statementQuiz = null;
    this.correctAnswers = [];
  }

  isEmpty(): boolean {
    return this.statementQuiz == null;
  }
}
