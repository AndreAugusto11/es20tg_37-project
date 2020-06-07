import Image from '@/models/management/Image';

export default class Justification {
  id: number | null = null;
  image: Image | null = null;
  content: string = '';
  name: string | null = null;

  constructor(jsonObj?: Justification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.image = jsonObj.image;
      this.content = jsonObj.content;
      this.name = jsonObj.name;
    }
  }
}
