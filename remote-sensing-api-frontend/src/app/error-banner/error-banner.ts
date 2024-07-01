import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from '../services/notification-service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'error-banner',
  templateUrl: './error-banner.html',
  styleUrls: ['./error-banner.css'],
})
export class ErrorBanner implements OnInit, OnDestroy {
  errorMessage: string = '';
  errorStatus: number | null = null;
  errorMessageNotification: string = '';
  errorStatusNotification: number | null = null;

  private errorTimeout: any;
  private subscription: Subscription | null = null;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.subscription = this.notificationService.error$.subscribe((error) => {
      this.errorMessageNotification = error.message;
      this.errorStatusNotification = error.status;
      this.getBannerClass();

      if (this.errorTimeout) {
        clearTimeout(this.errorTimeout);
      }

      this.errorTimeout = setTimeout(() => {
        this.closeBanner();
      }, 10000);
    });
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
    }
  }

  closeBanner() {
    this.errorMessage = '';
    this.errorStatus = null;
  }

  getBannerClass(): string {
    this.errorStatus = this.errorStatusNotification;
    if (this.errorStatusNotification == 404) {
      this.errorMessage = 'Detection failed. No objects found.';
      return 'error-banner error-banner-warning';
    } else {
      this.errorMessage =
        'General technical error. See server logs for more details.';
      return 'error-banner';
    }
  }
}
