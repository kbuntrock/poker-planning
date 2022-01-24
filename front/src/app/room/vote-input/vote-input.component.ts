import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { VoteValue } from '../../model/vote-value';

export interface Card {
  numero: number;
  color: string;
  selected: boolean;
}

@Component({
  selector: 'app-vote-input',
  templateUrl: './vote-input.component.html',
  styleUrls: ['./vote-input.component.scss']
})
export class VoteInputComponent implements OnInit {

  @Output()
  voteEvent = new EventEmitter<number>();

  @Input()
  inputMode: 'FREE' | 'CARD' = 'FREE';

  @Input()
  myVote: number;

  @Input()
  voteValues: Array<VoteValue>;

  cards: Array<Card>;

  constructor() { }

  ngOnInit(): void { }

  ngOnChanges(): void {
    if (this.voteValues) {
      this.cards = [];
      this.voteValues.forEach(e => this.cards.push({numero: e.value, color: e.getColor(), selected: false}));
    }
  }

  voterInput(vote: string) {
    this.voteEvent.emit(+vote);
  }

  selectCard(numero: number) {
    this.cards.forEach(c => {
      if(c.numero === numero){
        c.selected = true;
      } else {
        c.selected = false;
      }
    });
    this.voteEvent.emit(numero);
  }

  /*
  voterCard() {
    let vote = undefined;
    this.cards.forEach(c => {
      if(c.selected){
        vote = c.numero;
      }
    });
    if(vote){
      this.voteEvent.emit(vote);
    }

  }*/

}
