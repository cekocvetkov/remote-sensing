import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface STACItemPreview {
  id: string;
  thumbnailUrl: string;
  collection: string;
  downloadUrl: string;
}

@Injectable({
  providedIn: 'root',
})
export class StacService {
  constructor(private http: HttpClient) {}

  public getStacItems(
    extent: number[],
    dateFrom: string,
    dateTo: string,
    cloudCoverage: number
  ): Observable<STACItemPreview[]> {
    return this.http.post<STACItemPreview[]>(
      'http://localhost:8080/api/v1/remote-sensing/stac/items',
      {
        extent: extent,
        dateFrom: dateFrom,
        dateTo: dateTo,
        cloudCoverage: cloudCoverage,
      }
    );
  }

  public objectDetection(
    id: string,
    extent: number[],
    model: string
  ): Observable<Blob> {
    return this.http.post(
      'http://localhost:8080/api/v1/remote-sensing/stac/detection',
      {
        extent: extent,
        id: id,
        model: model,
      },
      {
        responseType: 'blob',
      }
    );
  }

  bingObjectDetection(img: string, model: string): Observable<Blob> {
    console.log(model);
    const formData = this.constructFormData(img, model);
    console.log(formData);
    return this.http.post(
      `http://localhost:8080/api/v1/remote-sensing/stac/bing/detection`,
      formData,
      { responseType: 'blob' }
    );
  }

  private constructFormData(base64Img: string, model: string) {
    base64Img = base64Img.replace('data:image/png;base64,', '');
    // Decode base64 string to Blob
    const byteCharacters = atob(base64Img);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: 'image/png' });

    // Create FormData and append the image
    const file = new File([blob], 'image.png');
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileName', 'image.png');
    formData.append('model', model);
    return formData;
  }
}
