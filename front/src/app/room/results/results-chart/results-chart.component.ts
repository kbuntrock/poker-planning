import { Component, Input, OnInit } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';
import { User } from '../../../../shared/websocket.service';

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
  voteValues: Array<number>;

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
      backgroundColor: 'rgba(67, 160, 71, 0.8)',
      hoverBackgroundColor: 'rgba(67, 160, 71, 0.8)',
      hoverBorderColor: 'rgba(67, 160, 71, 0.8)',
      borderWidth: 5,
      hoverBorderWidth: 5,
      borderColor: 'rgba(67, 160, 71, 1)' }
  ];

  constructor() { }

  ngOnInit(): void {

    console.info("display : ");
    console.info(this.votesMap);

    const label: Array<string[]> = [];
    const data: Array<number> = [];

    this.voteValues.forEach((v, k, m) => {
      //data.push(0);
      //label.push([]);
    });


    this.votesMap.forEach((value, key, map) => {
      console.info(key);
      let l = new Array<string>();
      l.push(''+key);
      value.forEach((v, i, a) => {
        l.push(v.displayName);
      });
      label.push(l);
      data.push(value.length);
    });
    this.barChartLabels = label;
    this.barChartData[0].data = data;

  }

}
