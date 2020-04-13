export class ClarificationRequestAnswer {
    id: number | null = null;
    content: string = '';
    // type
    name: string | null = null;
    username: string | null = null;

    constructor(jsonObj?: ClarificationRequestAnswer) {
        if (jsonObj) {
            this.id = jsonObj.id;
            // type
            this.content = jsonObj.content;
            this.name = jsonObj.name;
            this.username = jsonObj.username;
        }
    }
}