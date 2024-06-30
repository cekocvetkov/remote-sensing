import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private errorSubject: Subject<{ message: string, status: number }> = new Subject<{ message: string, status: number }>();

  constructor() { }

  showError(error: { message: string, status: number }): void {
    this.errorSubject.next(error);
  }

  get error$() {
    return this.errorSubject.asObservable();
  }

}
