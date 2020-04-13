import { Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  endpoint = 'http://localhost:8080/api/v1';
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  currentUser = {};

  constructor(private http: HttpClient, public router: Router) {}

  modulePostRequest(resource: string, request: string): Observable<any> {
    const api = `${this.endpoint}/module/1/` + resource;
    return this.http.post<any>(api, request).pipe(
      map((res: Response) => {
        return res || {};
      }),
      catchError(this.handleError)
    );
  }

  submitSqlInjectionTutorialQuery(query: string): Observable<any> {
    return this.modulePostRequest('query', query);
  }

  getModules(): Observable<any> {
    const api = `${this.endpoint}/modules/`;
    return this.http.get(api, { headers: this.headers }).pipe(
      map((res: Response) => {
        return res || {};
      }),
      catchError(this.handleError)
    );
  }

  // Error
  handleError(error: HttpErrorResponse) {
    let msg = '';
    if (error.error instanceof ErrorEvent) {
      // client-side error
      msg = error.error.message;
    } else {
      // server-side error
      msg = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(msg);
  }
}
