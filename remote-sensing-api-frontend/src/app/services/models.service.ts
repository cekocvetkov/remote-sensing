import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
export interface Model {
  modelName: string;
  displayName: string;
  epochsTrained: number;
  detectionType: string;
}

@Injectable({
  providedIn: 'root',
})
export class ModelService {
  constructor(private http: HttpClient) {}

  public getAvailableModels(): Observable<Model[]> {
    return this.http.get<Model[]>(
      'http://localhost:8080/api/v1/remote-sensing/models'
    );
  }
}
