import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private errorSubject: Subject<string> = new Subject<string>();

  constructor() { }

  showError(message: string): void {
    this.errorSubject.next(message);
  }

  get error$() {
    return this.errorSubject.asObservable();
  }

}
