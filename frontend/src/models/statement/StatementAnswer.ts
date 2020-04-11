import { QuestionAnswer } from '@/models/management/QuestionAnswer';

export default class StatementAnswer {
  public questionAnswerId: number | null = null;
  public questionAnswerDto: QuestionAnswer;
  public optionId: number | null = null;
  public timeTaken: number = 0;
  public sequence!: number;

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);
      this.optionId = jsonObj.optionId;
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
    }
  }
}
