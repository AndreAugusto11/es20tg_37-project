export const enum TYPE {
    TEACHER = "TEACHER_ANSWER",
    STUDENT = "STUDENT_ANSWER"
}

export class ClarificationRequestAnswer {
    id: number | null = null;
    content: string = '';
    type: TYPE | null = null;
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