import { ISOtoString } from '@/services/ConvertDateService';
import TopicConjunction from '@/models/management/TopicConjunction';

export class Tournament {
  id: number | null = null;
  title: string | null = null;
  creatorId: number | null = null;
  creatorName: string | null = null;
  numberOfQuestions!: number;
  numberOfAvailableQuestions: number | null = null;
  creationDate: string | null = null;
  availableDate!: string;
  conclusionDate!: string;
  resultsDate: string | null = null;
  status: string | null = null;
  topicConjunctions: TopicConjunction[] = [];
  enrolledStudentsIds!: number[];
  enrolledStudentsNames!: string[];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.creatorId = jsonObj.creatorId;
      this.creatorName = jsonObj.creatorName;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAvailableQuestions = jsonObj.numberOfAvailableQuestions;
      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
      if (jsonObj.resultsDate)
        this.resultsDate = ISOtoString(jsonObj.resultsDate);
      this.status = jsonObj.status;
      this.topicConjunctions = jsonObj.topicConjunctions.map(
        (topicConjunctionsDto: TopicConjunction) => {
          return new TopicConjunction(topicConjunctionsDto);
        }
      );
      this.enrolledStudentsIds = jsonObj.enrolledStudentsIds;
      this.enrolledStudentsNames = jsonObj.enrolledStudentsNames;
    }
  }
}