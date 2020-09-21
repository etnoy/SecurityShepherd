import { UrlSegment } from '@angular/router';
export class Module {
  id: string;
  name: string;
  shortName: string;
  parameters: UrlSegment[];
  description: string;
  isSolved: boolean;
}
