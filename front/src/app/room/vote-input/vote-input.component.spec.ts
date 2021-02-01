import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VoteInputComponent } from './vote-input.component';

describe('VoteInputComponent', () => {
  let component: VoteInputComponent;
  let fixture: ComponentFixture<VoteInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VoteInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VoteInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
