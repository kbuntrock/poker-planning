import { EventEmitter, Output } from '@angular/core';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {

  @Input()
  numero: number;

  @Input()
  selected: boolean = false;

  @Input()
  voted: boolean = false;

  @Output()
  selectedNumber = new EventEmitter<number>();


  constructor() { }

  ngOnInit(): void {
  }

  select() {
    if(!this.selected) {
      this.selectedNumber.emit(this.numero);
    }

  }

}
