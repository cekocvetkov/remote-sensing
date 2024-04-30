import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { MainComponent } from './main/main.component';
import { MapComponent } from './map/map.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { ImageCropperModule } from 'ngx-image-cropper';
import { NgxCaptureModule } from 'ngx-capture';
import { ItemsPreviewComponent } from './items-preview/items-preview.component';
import { ErrorInterceptor } from './services/error-interceptor';
import {ErrorBanner} from "./error-banner/error-banner";

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    MapComponent,
    ItemsPreviewComponent,
    ErrorBanner
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    ImageCropperModule,
    NgxCaptureModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
