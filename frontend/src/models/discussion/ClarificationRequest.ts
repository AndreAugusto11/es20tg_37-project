import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import Image from '@/models/management/Image';
import { ClarificationRequestAnswer } from '@/models/discussion/ClarificationRequestAnswer';

export class ClarificationRequest {
  id: number | null = null;
  content: string = '';
  questionAnswerDto!: QuestionAnswer;
  name: string | null = null;
  username: string | null = null;
  status: string = 'OPEN';
  image: Image | null = null;
  clarificationRequestAnswerDto!: ClarificationRequestAnswer;

  constructor(jsonObj?: ClarificationRequest) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.status = jsonObj.status;
      this.image = jsonObj.image;
      this.clarificationRequestAnswerDto = new ClarificationRequestAnswer(jsonObj.clarificationRequestAnswerDto);
    }
  }
}