import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StoryCreatorComponent } from './story-creator.component';

describe('StoryCreatorComponent', () => {
  let component: StoryCreatorComponent;
  let fixture: ComponentFixture<StoryCreatorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StoryCreatorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StoryCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
