import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultsBoardComponent } from './results-board.component';

describe('ResultsBoardComponent', () => {
  let component: ResultsBoardComponent;
  let fixture: ComponentFixture<ResultsBoardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ResultsBoardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultsBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
