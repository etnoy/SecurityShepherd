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

  submitSqlInjectionTutorialQuery(query: string): Observable<any> {
    const api = `${this.endpoint}/module/sql-injection-tutorial/query`;
    return this.http.post<any>(api, query).pipe(
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
