import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import {
  Observable,
  catchError,
  of,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs';
import { SentinelService } from '../services/sentinel.service';
import { MapSource, SentinelRequest } from './main.component';
import { StacService } from '../services/stac.service';

export interface STACItemPreview {
  id: string;
  thumbnailUrl: string;
  collection: string;
  downloadUrl: string;
}

export interface MainState {
  selectedItem: Blob | null;
  currentExtent: number[];
  items: STACItemPreview[];
  class: string;
  loading: boolean;
  objectDetectionImageUrl: string | null;
  error: string | null;
  mapSource: string;
  dataSource: string;
  detection: string;
}

export const initialState: MainState = {
  selectedItem: null,
  currentExtent: [],
  items: [],
  class: '',
  loading: false,
  objectDetectionImageUrl: null,
  error: null,
  mapSource: '',
  dataSource: '',
  detection: '',
};

@Injectable()
export class MainStore extends ComponentStore<MainState> {
  constructor(
    private sentinelService: SentinelService,
    private stacService: StacService
  ) {
    super(initialState);
  }

  private loading$ = this.select((state) => state.loading);
  private error$ = this.select((state) => state.error);
  private items$ = this.select((state) => state.items);
  private class$ = this.select((state) => state.class);
  private selectedItem$ = this.select((state) => state.selectedItem);
  private currentExtent$ = this.select((state) => state.currentExtent);
  private objectDetectionImageUrl$ = this.select(
    (state) => state.objectDetectionImageUrl
  );
  private mapSource$ = this.select((state) => state.mapSource);
  private dataSource$ = this.select((state) => state.dataSource);
  private detection$ = this.select((state) => state.detection);

  public vm$ = this.select({
    loading: this.loading$,
    error: this.error$,
    items: this.items$,
    class: this.class$,
    selectedItem: this.selectedItem$,
    currentExtent: this.currentExtent$,
    objectDetectionImageUrl: this.objectDetectionImageUrl$,
    mapSource: this.mapSource$,
    dataSource: this.dataSource$,
    detection: this.detection$,
  });

  private setLoading = this.updater((state, isLoading: boolean) => ({
    ...state,
    loading: isLoading,
  }));

  private setObjectDetectionImageUrl = this.updater(
    (state, newObjectDetectionImageUrl: string) => ({
      ...state,
      objectDetectionImageUrl: newObjectDetectionImageUrl,
    })
  );

  private setError = this.updater((state, errorMessage: string) => ({
    ...state,
    loading: false,
    error: errorMessage,
  }));

  private setClass = this.updater((state, classNew: string) => ({
    ...state,
    loading: false,
    class: classNew,
  }));

  private setCurrentExtent = this.updater((state, extent: number[]) => ({
    ...state,
    currentExtent: extent,
  }));

  private setSelectedItem = this.updater((state, item: Blob) => ({
    ...state,
    loading: false,
    selectedItem: item,
  }));

  private setMapSourceType = this.updater((state, sourceType: string) => ({
    ...state,
    loading: false,
    mapSource: sourceType,
  }));

  private setDataSourceType = this.updater((state, dataSourceType: string) => ({
    ...state,
    loading: false,
    dataSource: dataSourceType,
  }));

  private setDetectionType = this.updater((state, detectionType: string) => ({
    ...state,
    loading: false,
    detection: detectionType,
  }));
  private setItems = this.updater((state, items: STACItemPreview[]) => ({
    ...state,
    loading: false,
    items: items,
  }));

  readonly mapSource = this.effect((sourceType$: Observable<MapSource>) => {
    return sourceType$.pipe(
      tap((sourceType) => {
        this.setMapSourceType(sourceType.name);
      })
    );
  });

  readonly dataSource = this.effect((sourceType$: Observable<string>) => {
    return sourceType$.pipe(
      tap((sourceType) => {
        if (sourceType === 'BING') {
          this.setMapSourceType('Aerial');
        }
        this.setDataSourceType(sourceType);
      })
    );
  });

  readonly detectionType = this.effect((sourceType$: Observable<string>) => {
    return sourceType$.pipe(
      tap((sourceType) => {
        this.setDetectionType(sourceType);
      })
    );
  });

  readonly bingObjectDetection = this.effect((base64: Observable<string>) => {
    return base64.pipe(
      // tap((s) => console.log(s)),
      tap(() => this.setLoading(true)),
      withLatestFrom(this.detection$),
      switchMap(([base64, model]: [string, string]) => {
        return this.stacService.bingObjectDetection(base64, model).pipe(
          tap({
            next: (image: Blob) => {
              this.setSelectedItem(image);
              // const imageUrl = URL.createObjectURL(image);
              // this.setObjectDetectionImageUrl(imageUrl);
              // this.setLoading(false);
            },
            error: (e) => {
              this.setError(e);
            },
          }),
          catchError((e) => {
            return of(e);
          })
        );
      })
    );
  });

  readonly loadImage = this.effect(
    (sentinelRequest$: Observable<SentinelRequest>) => {
      return sentinelRequest$.pipe(
        tap((sentinelRequest: SentinelRequest) =>
          console.log(
            `Get image for the following request: ${sentinelRequest.extent}, ${sentinelRequest.dateFrom}, ${sentinelRequest.dateTo}, ${sentinelRequest.cloudCoverage}`
          )
        ),
        tap(() => this.setLoading(true)),
        tap((sentinelRequest: SentinelRequest) =>
          this.setCurrentExtent(sentinelRequest.extent!)
        ),
        withLatestFrom(this.dataSource$, this.detection$),
        switchMap(([sentinelRequest, datasource, detectionModel]) => {
          if (datasource === 'STAC') {
            console.log('Datasource STAC');
            return this.stacService.getStacItems(
              sentinelRequest.extent!,
              sentinelRequest.dateFrom.toString(),
              sentinelRequest.dateTo.toString(),
              sentinelRequest.cloudCoverage
            )
            .pipe(
              tap({
                next: (items: STACItemPreview[]) => {
                  this.setItems(items);
                },
                error: (e) => this.setError(e),
              }),
              catchError((e) => {
                return of(e);
              })
            );
          }
          console.log('Datasource Sentinel Processing API');
          return this.sentinelService
            .getSentinelGeoTiff(
              sentinelRequest.extent!,
              sentinelRequest.dateFrom.toString(),
              sentinelRequest.dateTo.toString(),
              sentinelRequest.cloudCoverage,
              detectionModel
            )
            .pipe(
              tap({
                next: (image: Blob) => this.setSelectedItem(image),
                error: (e) => this.setError(e),
              }),
              catchError((e) => {
                return of(e);
              })
            );
        })
      );
    }
  );

  readonly loadImageSTAC = this.effect((itemId$: Observable<string>) => {
    return itemId$.pipe(
      tap((itemId) => console.log(`Get image for item with id ${itemId}`)),
      tap(() => this.setLoading(true)),
      withLatestFrom(this.currentExtent$, this.detection$),
      tap(([_, currentExtent, detection]) =>
        console.log(`... and extent ${currentExtent}`)
      ),
      switchMap(
        ([itemId, currentExtent, model]: [string, number[], string]) => {
          return this.stacService
            .objectDetection(itemId, currentExtent, model)
            .pipe(
              tap({
                next: (image: Blob) => this.setSelectedItem(image),
                error: (e) => this.setError(e),
              }),
              catchError((e) => {
                return of(e);
              })
            );
        }
      )
    );
  });
}
