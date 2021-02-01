import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-vote-input',
  templateUrl: './vote-input.component.html',
  styleUrls: ['./vote-input.component.scss']
})
export class VoteInputComponent implements OnInit {

  @Output() 
  voteEvent = new EventEmitter<number>();

  constructor() { }

  ngOnInit(): void {
  }

  voter(vote: string) {
    this.voteEvent.emit(+vote);
  }

}
