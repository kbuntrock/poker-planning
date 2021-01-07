import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SocketClientState, WebsocketService } from '../../shared/websocket.service';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss']
})
export class RoomComponent implements OnInit, OnDestroy {

  roomId: string;

  subscription: Subscription = new Subscription();

  constructor(private readonly route: ActivatedRoute, private readonly wsService: WebsocketService) { }

  ngOnInit(): void {
    this.roomId = this.route.snapshot.params['roomId'];
    if(this.wsService.getClientState() !== SocketClientState.CONNECTED){
      // La connexion est en cours, on rejoindra la room quand elle sera effective
      this.subscription.add(this.wsService.getClientState$().subscribe(state => {
        if(state === SocketClientState.CONNECTED){
          this.joinRoom();
        }
      }));
    }else {
      // On est déjà connecté, on rejoint la room directement
      this.joinRoom();
    }
    
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private joinRoom() {
    this.wsService.joinRoom(this.roomId, (message) => {
      console.info("roomJoinded!");
      console.info(message);
    });
  }

}
