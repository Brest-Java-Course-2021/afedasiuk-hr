import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {DepartmentDto} from "../model/department-dto";
import {catchError, map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private basePath = 'http://localhost:8088';

  constructor(protected httpClient: HttpClient) {
  }

  public getDepartmentDTOs(): Observable<Array<DepartmentDto>> {
    return this.httpClient.get<Array<DepartmentDto>>(`${this.basePath}/department-dtos`)
      .pipe(
        map(response => response || []),
        tap(response => console.log(JSON.stringify(response))),
        catchError(this.handleError)
      );
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    let errorMessage: string;

    // A client-side or network error occurred.
    if (err.error instanceof Error) {
      errorMessage = `An error occurred: ${err.error.message}`;
    }
      // The backend returned an unsuccessful response code.
    // The response body may contain clues as to what went wrong,
    else {
      errorMessage = `Backend returned code ${err.status}, body was: ${err.error}`;
    }
    console.error(errorMessage);
    return Observable.throw(errorMessage);
  }

}
