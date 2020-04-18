export class Tournament {
  id: number | null = null;
  creatorID: number | null = null;
  numQuests: number | null = null;
  startTime!: string;
  endTime!: string;
  status!: string;

  constructor(jsonObj?: Tournament) {
    if(jsonObj) {
      this.id = jsonObj.id;
      this.creatorID = jsonObj.creatorID;
      this.numQuests = jsonObj.numQuests;
      this.startTime = jsonObj.startTime;
      console.log(jsonObj.startTime,typeof jsonObj.startTime);
      this.endTime = jsonObj.endTime;
      this.status = jsonObj.status;
    }
  }


}