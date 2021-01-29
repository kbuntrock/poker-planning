import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SocketClientState, User, WebsocketService, WSMessage } from '../../shared/websocket.service';
import { Clipboard } from '@angular/cdk/clipboard';
import { PropertiesService } from '../common/properties.service';


@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss']
})
export class RoomComponent implements OnInit, OnDestroy {

  roomId: string;

  // Indique si l'utilisateur peut proposer des storys
  isScrumMaster: boolean = false;
  creatingNewRoom: boolean = false;

  users: Array<User>;
  creator: User;
  storyLabel: string = "..."

  subscription: Subscription = new Subscription();

  constructor(private readonly route: ActivatedRoute, private readonly wsService: WebsocketService, 
    private clipboard: Clipboard, private readonly appProperties: PropertiesService) { }

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
      const response: WSMessage = JSON.parse(message.body);
      this.users = response.connectedUsers;
      this.creator = response.creator;
      this.isScrumMaster = this.creator.name === this.appProperties.getUserId();
    });
  }

  copyToClipboard() {
    this.clipboard.copy('http://localhost:4200/room/' + this.roomId);
  }

  changerUS(libelleUS: string) {
    this.storyLabel = libelleUS;
    this.wsService.startNewStory(this.storyLabel, (message) => {
      console.info(message);
    });
  }

}
