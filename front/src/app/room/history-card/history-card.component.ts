import { Component, Inject, Input, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { VoteValue } from 'src/app/model/vote-value';

@Component({
  selector: 'app-history-card',
  templateUrl: './history-card.component.html',
  styleUrls: ['./history-card.component.scss']
})
export class HistoryCardComponent implements OnInit {

  name: string;
  votesMap: Map<string, number>;
  voteValues: Array<VoteValue>;
  chosenValue: number;

  constructor(@Inject(MAT_DIALOG_DATA) public data: {
    name: string,
    voteValues: Array<VoteValue>,
    votesMap: Map<string, number>,
    chosenValue: number
  }) { 
    this.name = data.name;
    this.voteValues = data.voteValues;
    this.votesMap = data.votesMap;
    this.chosenValue = data.chosenValue;
  }

  ngOnInit(): void { }

}
