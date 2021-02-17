import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

export interface Card {
  numero: number;
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

  cards: Array<Card> = [
    {numero: 1, selected: false},
    {numero: 2, selected: false},
    {numero: 3, selected: false},
    {numero: 5, selected: false},
    {numero: 8, selected: false},
    {numero: 13, selected: false},
    {numero: 21, selected: false},
    {numero: 34, selected: false},
    {numero: 55, selected: false},
    {numero: 89, selected: false},
    {numero: 144, selected: false}
  ];

  constructor() { }

  ngOnInit(): void {
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
    //this.voter(numero.toString());
  }

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
    
  }

}
