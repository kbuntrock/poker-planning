import { Component, OnInit, OnDestroy } from '@angular/core';
import { PropertiesService } from '../properties.service';
import { Subscription } from 'rxjs';
import { WebsocketService } from '../../../shared/websocket.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  private subscriptions = new Subscription();
  public showMenu = false;

  constructor(private readonly propertiesService: PropertiesService, private wsService: WebsocketService) { }

  ngOnInit(): void {
    this.subscriptions.add(this.propertiesService.getUsername$().subscribe(username => {
      this.showMenu = username !== undefined;
    }));
  }

  ngOnDestroy(): void {
   this.subscriptions.unsubscribe();
  }

  public deconnecter(): void {
    this.wsService.disconnect();
    this.propertiesService.eraseUsername();
  }

}
