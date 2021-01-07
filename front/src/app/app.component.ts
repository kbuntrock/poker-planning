import { Component, OnInit, OnDestroy } from '@angular/core';
import { PropertiesService } from './common/properties.service';
import { Subscription } from 'rxjs';
import { SocketClientState, WebsocketService } from '../shared/websocket.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  public title = 'poker-planning';
  public username: string = undefined;

  private subscriptions = new Subscription();

  constructor(
    private readonly appProperties: PropertiesService, 
    private readonly wsService: WebsocketService, 
    private readonly router: Router, 
    private readonly route: ActivatedRoute) {
  }

  ngOnInit(): void {

    this.subscriptions.add(this.appProperties.getUsername$().subscribe(username => {
      this.username = this.appProperties.getUsername();
      if(this.username){
       if(this.wsService.getClientState() === SocketClientState.DISCONNECTED){
        this.wsService.connect();
       }
      } else {
        this.router.navigate(['login'], { relativeTo: this.route });
      }
    }));

    this.subscriptions.add(this.wsService.getClientState$().subscribe(state => {
      
      
      switch(state) {
        case SocketClientState.ATTEMPTING_DISCONNECTION :
          console.info('SocketClientState.ATTEMPTING_DISCONNECTION');
          break;
        case SocketClientState.ATTEMPTING_CONNECTION :
          console.info('SocketClientState.ATTEMPTING_CONNECTION');
          break;
        case SocketClientState.CONNECTED :
          console.info('SocketClientState.CONNECTED');
          this.router.navigate(['join-room'], { relativeTo: this.route });
          break;
        case SocketClientState.DISCONNECTED :
          console.info('SocketClientState.DISCONNECTED');
            break;
      }
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  
}
