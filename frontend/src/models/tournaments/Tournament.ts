export class Tournament {
  id: number | null = null;
  creatorID: number | null = null;
  numQuests: number | null = null;
  topics: number[] | null = null;
  topicsName: string[] | null = null;
  enrolledStudentsIds!: number[];
  startTime!: number[];
  endTime!: number[];
  startTimeString!: string;
  endTimeString!: string;
  status!: string;

  constructor(jsonObj?: Tournament) {
    if(jsonObj) {
      this.id = jsonObj.id;
      this.creatorID = jsonObj.creatorID;
      this.numQuests = jsonObj.numQuests;
      this.enrolledStudentsIds = jsonObj.enrolledStudentsIds;
      this.topics = jsonObj.topics;
      this.topicsName = jsonObj.topicsName;
      this.startTime = jsonObj.startTime;
      this.endTime = jsonObj.endTime;
      this.getDateFormatted(jsonObj);
      this.status = jsonObj.status;
    }
  }

  getDateFormatted(t: Tournament):void{
    let start = String(t.startTime).split(',');
    let end = String(t.endTime).split(',');
    this.startTimeString = start[0] + '-' + start[1] + '-' + start[2] + ' ' + start[3]+ ':'+start[4];
    this.endTimeString = end[0] + '-' + end[1] + '-' + end[2] + ' ' + end[3]+ ':'+end[4];
  }

}