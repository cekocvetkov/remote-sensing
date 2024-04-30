import {
  Component,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';

import { MapService } from '../services/map.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})
export class MapComponent implements OnChanges {
  @Input() loading: boolean = false;
  @Input() loadedImage: Blob | null = null;
  @Input() currentExtent: number[] = [];
  @Input() mapSource: string = '';
  @Output() drawEnd$ = this.mapService.drawEnd$;

  constructor(private mapService: MapService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['mapSource']) {
      this.mapService.changeMapSource(this.mapSource);
    }
    if (changes['loadedImage']) {
      console.log(this.currentExtent);
      this.mapService.setSource(this.loadedImage, this.currentExtent);
    }
  }
}
