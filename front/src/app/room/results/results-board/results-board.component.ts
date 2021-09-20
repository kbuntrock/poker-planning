import { Component, Input, OnInit } from '@angular/core';
import { User } from '../../../../shared/websocket.service';

@Component({
  selector: 'app-results-board',
  templateUrl: './results-board.component.html',
  styleUrls: ['./results-board.component.scss']
})
export class ResultsBoardComponent implements OnInit {

  @Input()
  votesMap = new Map<number, Array<User>>();

   /**
   * Toutes les valeurs de vote qui sont possibles
   */
  @Input()
  voteValues: Array<number>;

  minimumValue: number;
  minimumValueVoters: string;
  maximumValue: number;
  maximumValueVoters: string;
  averageValue: number = 12.5;

  constructor() { }

  ngOnInit(): void {
    this.votesMap = this.sortMapByKeys(this.votesMap);

    const keys = Array.from(this.votesMap.keys());
    this.minimumValue = keys[0];
    this.minimumValueVoters = this.votesMap.get(this.minimumValue).map(v => v.displayName).join(', ');
    this.maximumValue = keys[keys.length-1];
    this.maximumValueVoters = this.votesMap.get(this.maximumValue).map(v => v.displayName).join(', ');

    let total = 0;
    let nb = 0;

    this.votesMap.forEach((v, k) => {
      total += k * v.length;
      nb += v.length;
    });

    this.averageValue = total/nb;

  }

  sortMapByKeys(map: Map<number, Array<User>>): Map<number, Array<User>> {
    const sortedArray = [];
    map.forEach((value, key, map) => {
      sortedArray.push(key)
    });
    sortedArray.sort((a, b) => {
      return a - b;
    });

    const sortedMap = new Map<number, Array<User>>();
    sortedArray.forEach((v, i, array) => {
      sortedMap.set(v, map.get(v));
    });
    return sortedMap;
  }

}
