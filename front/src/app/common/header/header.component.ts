import { Component, OnInit, OnDestroy } from '@angular/core';
import { PropertiesService, RoomInfo } from '../properties.service';
import { Subscription } from 'rxjs';
import { WebsocketService } from '../../../shared/websocket.service';
import { Router } from '@angular/router';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {

  private subscriptions = new Subscription();
  public showMenu = false;

  public roomInfos: RoomInfo;

  constructor(private readonly propertiesService: PropertiesService,
    private readonly wsService: WebsocketService,
    private readonly router: Router,
    private clipboard: Clipboard) { }

  ngOnInit(): void {
    this.subscriptions.add(this.propertiesService.getUsername$().subscribe(username => {
      this.showMenu = username !== undefined;
    }));
    this.subscriptions.add(this.propertiesService.getRoomInfos$().subscribe(infos => {
      this.roomInfos = infos;
    }));
  }

  ngOnDestroy(): void {
   this.subscriptions.unsubscribe();
  }

  public deconnecter(): void {
    this.wsService.disconnect();
    this.propertiesService.disconnect();
    this.router.navigate(['login']);
  }

  copyToClipboard() {
    console.info('copy url : ' + this.roomInfos.url);
    this.clipboard.copy(this.roomInfos.url);
  }

}
