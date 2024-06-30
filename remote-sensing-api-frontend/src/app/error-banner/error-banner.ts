import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../services/notification-service';

@Component({
  selector: 'error-banner',
  templateUrl: './error-banner.html',
  styleUrls: ['./error-banner.css'],
})
export class ErrorBanner implements OnInit {

  errorMessage: string = '';
  errorStatus: number | null = null;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationService.error$.subscribe((error) => {
      this.errorMessage = error.message;
      this.errorStatus = error.status;
    });
  }

  closeBanner() {
    this.errorMessage = '';
    this.errorStatus = null;
  }

  getBannerClass(): string {
    if (!this.errorStatus) return '';
    if (this.errorStatus == 404) {
      this.errorMessage = 'Detection failed. No class found.'
      return 'error-banner error-banner-warning';
    } else {
      this.errorMessage = 'General technical error. See server logs for more details.'
      return 'error-banner';
    }
  }
}

