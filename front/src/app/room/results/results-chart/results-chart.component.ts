import { Component, Input, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';
import { User } from '../../../../shared/websocket.service';
import { VoteValue } from '../../../model/vote-value';

@Component({
  selector: 'app-results-chart',
  templateUrl: './results-chart.component.html',
  styleUrls: ['./results-chart.component.scss']
})
export class ResultsChartComponent implements OnInit {

  @Input()
  votesMap = new Map<number, Array<User>>();

  /**
   * Toutes les valeurs de vote qui sont possibles
   */
  @Input()
  voteValues: Array<VoteValue>;

  /**
   * Défini la valeur max en y. Si non défini, sera le plus grand nombre de votes
   */
  @Input()
  maxAxisY: number;

  zoom: boolean = true;

  public barChartOptions: ChartOptions = {
    responsive: true,
    // We use these empty structures as placeholders for dynamic theming.
    scales: { xAxes: [{
      scaleLabel: {
        display: true,
        labelString: 'Cartes'
      }
    }], yAxes: [
      { ticks:
        { beginAtZero: true,
          stepSize: 1
        },
        scaleLabel: {
          display: true,
          labelString: 'Nombre de voix'
        }
      }] },
    plugins: {
      datalabels: {
        anchor: 'end',
        align: 'end',
      }
    }
  };
  public barChartLabels: Label[] = [];
  public barChartType: ChartType = 'bar';
  public barChartLegend = false;
  public barChartData: ChartDataSets[] = [
    { data: [],
      label: 'Nombre de voix',
      backgroundColor: [],
      hoverBackgroundColor: [],
      hoverBorderColor: [],
      borderWidth: 5,
      hoverBorderWidth: 5,
      borderColor: [] }
  ];

  constructor() { }

  ngOnInit(): void {

    // Réglage max sur l'axe y
    if(this.maxAxisY) {
      this.barChartOptions.scales.yAxes[0].ticks.max = this.maxAxisY;
    }

    const label: Array<string[]> = [];
    const data: Array<number> = [];

    let nbVoteValues: number = 0;

    this.voteValues.forEach((voteValue) => {
      if (this.zoom && nbVoteValues >= this.votesMap.size) return;

      let votes: Array<User> = this.votesMap.get(voteValue.value);
      let curLabel: string[] = [''+voteValue.value];

      if (votes) {
        nbVoteValues++;
        votes.forEach((vote, i, a) => {
          curLabel.push(vote.displayName);
        });
      } else {
        if (this.zoom && nbVoteValues === 0 ) return;
      }

      data.push(votes ? votes.length : 0);
      label.push(curLabel);

      (<String[]> this.barChartData[0].backgroundColor).push(voteValue.getColor());
    });

    this.barChartLabels = label;
    this.barChartData[0].data = data;
  }

}
