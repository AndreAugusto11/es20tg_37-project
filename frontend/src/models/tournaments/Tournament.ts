import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export class Tournament {
  id: number | null = null;
  creatorID: number | null = null;
  creatorName: string | null = null;
  numberQuestions: number | null = null;
  quizID: number | null = null;
  topics: Topic[] = [];
  enrolledStudentsIds!: number[];
  enrolledStudentsNames!: string[];
  startTime!: string;
  endTime!: string;
  status!: string;

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.creatorID = jsonObj.creatorID;
      this.creatorName = jsonObj.creatorName;
      this.numberQuestions = jsonObj.numberQuestions;
      this.quizID = jsonObj.quizID;
      this.enrolledStudentsIds = jsonObj.enrolledStudentsIds;
      this.enrolledStudentsNames = jsonObj.enrolledStudentsNames;
      this.topics = jsonObj.topics;
      if (jsonObj.startTime)
        this.startTime = ISOtoString(jsonObj.startTime);
      if (jsonObj.endTime)
        this.endTime = ISOtoString(jsonObj.endTime);
      this.status = jsonObj.status;
    }
  }
}