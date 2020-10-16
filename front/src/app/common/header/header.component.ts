import { Component, OnInit } from '@angular/core';
import { PropertiesService } from '../properties.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(private readonly propertiesService: PropertiesService) { }

  ngOnInit(): void {
  }

  public deconnecter(): void {
    console.info("deconnecter");
    this.propertiesService.eraseUsername();
  }

}
