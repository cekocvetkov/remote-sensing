import { Component, OnInit } from '@angular/core';
import {NotificationService} from "../services/notification-service";

@Component({
  selector: 'error-banner',
  templateUrl: './error-banner.html',
  styleUrl: './error-banner.css',
})
export class ErrorBanner implements OnInit {

  errorMessage: string = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationService.error$.subscribe(errorMessage => {
      this.errorMessage = errorMessage;
    });
  }

  closeBanner() {
    this.errorMessage = '';
  }
}
