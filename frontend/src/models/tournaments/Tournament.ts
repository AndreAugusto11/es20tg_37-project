export class Tournament {
  id: number | null = null;
  creatorID: number | null = null;
  numQuests: number | null = null;
  enrolledStudentsIds!: number[];
  topicsName: string | null = null;
  startTime!: string;
  endTime!: string;
  status!: string;

  constructor(jsonObj?: Tournament) {
    if(jsonObj) {
      this.id = jsonObj.id;
      this.creatorID = jsonObj.creatorID;
      this.numQuests = jsonObj.numQuests;
      this.enrolledStudentsIds = jsonObj.enrolledStudentsIds;
      this.topicsName = jsonObj.topicsName;
      this.getDateFormatted(jsonObj);
      this.status = jsonObj.status;
    }
  }

  getDateFormatted(t: Tournament):void{
    let start = String(t.startTime).split(',');
    let end = String(t.endTime).split(',');
    this.startTime = start[0] + '-' + start[1] + '-' + start[2] + ' ' + start[3]+ ':'+start[4];
    this.endTime = end[0] + '-' + end[1] + '-' + end[2] + ' ' + end[3]+ ':'+end[4];
  }

}