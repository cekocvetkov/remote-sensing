import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { STACItemPreview } from '../main/main.component.store';

@Component({
  selector: 'app-items-preview',
  templateUrl: './items-preview.component.html',
  styleUrl: './items-preview.component.css',
})
export class ItemsPreviewComponent implements OnInit {

  @Input() items: STACItemPreview[] = [];
  @Output() loadImage = new EventEmitter<string>();

  constructor() {}

  ngOnInit(): void {}

  onTriggerLoadImage(itemId: string) {
    this.loadImage.emit(itemId);
  }

}
