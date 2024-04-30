import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemsPreviewComponent } from './items-preview.component';

describe('ItemsPreviewComponent', () => {
  let component: ItemsPreviewComponent;
  let fixture: ComponentFixture<ItemsPreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ItemsPreviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ItemsPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
