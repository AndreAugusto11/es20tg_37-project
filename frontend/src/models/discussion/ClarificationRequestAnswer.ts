import { ISOtoString } from '@/services/ConvertDateService';
import Image from '@/models/management/Image';


export class ClarificationRequestAnswer {
    id: number | null = null;
    content: string = '';
    type: string | null = null;
    name: string | null = null;
    creationDate!: string | null;
    username: string | null = null;
    image: Image | null = null;

    constructor(jsonObj?: ClarificationRequestAnswer) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.type = jsonObj.type;
            this.content = jsonObj.content;
            this.name = jsonObj.name;
            this.username = jsonObj.username;
            this.creationDate = ISOtoString(jsonObj.creationDate);
            this.image = jsonObj.image;
        }
    }
}