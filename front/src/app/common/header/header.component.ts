import { Component, OnInit, OnDestroy } from '@angular/core';
import { PropertiesService } from '../properties.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  private subscriptions = new Subscription();
  public showMenu = false;

  constructor(private readonly propertiesService: PropertiesService) { }

  ngOnInit(): void {
    this.subscriptions.add(this.propertiesService.getUsername$().subscribe(username => {
      this.showMenu = username !== undefined;
    }));
  }

  ngOnDestroy(): void {
   this.subscriptions.unsubscribe();
  }

  public deconnecter(): void {
    console.info("deconnecter");
    this.propertiesService.eraseUsername();
  }

}
