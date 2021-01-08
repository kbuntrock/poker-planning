import { Component, OnInit, OnDestroy } from '@angular/core';
import { PropertiesService } from '../properties.service';
import { Subscription } from 'rxjs';
import { WebsocketService } from '../../../shared/websocket.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  private subscriptions = new Subscription();
  public showMenu = false;

  constructor(private readonly propertiesService: PropertiesService, 
    private readonly wsService: WebsocketService,
    private readonly router: Router) { }

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
    this.router.navigate(['login']);
  }

}
