import { UrlSegment } from '@angular/router';
export class Module {
  id: string;
  parameters: UrlSegment[];
  isSolved: boolean;
}
