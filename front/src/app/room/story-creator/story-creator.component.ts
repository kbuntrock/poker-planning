import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-story-creator',
  templateUrl: './story-creator.component.html',
  styleUrls: ['./story-creator.component.scss']
})
export class StoryCreatorComponent implements OnInit {

  @Input()
  libelleUS = '...';

  @Output() 
  newLibelleUS = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
  }

  changerUS(value: string) {
    this.newLibelleUS.emit(value);
  }

}
