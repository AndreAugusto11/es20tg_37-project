import Question from '@/models/management/Question';
import Justification from '@/models/management/Justification';

export default class QuestionSuggestion {
  id: number | null = null;
  questionDto: Question = new Question();
  status: string = 'PENDING';
  creationDate!: string | null;
  justificationDto: Justification | null = null;

  constructor(jsonObj?: QuestionSuggestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionDto = jsonObj.questionDto;
      this.status = jsonObj.status;
      this.creationDate = jsonObj.creationDate;
      this.justificationDto = jsonObj.justificationDto;
    }
  }
}
