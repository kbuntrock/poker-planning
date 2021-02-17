import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SocketClientState, User, WebsocketService, WSMessage } from '../../shared/websocket.service';
import { Clipboard } from '@angular/cdk/clipboard';
import { PropertiesService } from '../common/properties.service';
import { environment } from '../../environments/environment';


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
  aVote: boolean = true;

  users: Array<User>;
  usersMap = new Map<string, User>();
  creator: User;
  storyLabel: string = "..."

  votesMap = new Map<number, Array<User>>();
  votesArray: Array<string> = undefined;

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
      this.onPlanningMessage(message.body);
    });
  }

  onPlanningMessage(message: string) {
    const response: WSMessage = JSON.parse(message);
    switch(response.type) {
      case 'FULL' :
        this.users = this.mapUserArray(response.connectedUsers);
        this.creator = response.creator;
        this.isScrumMaster = this.creator.name === this.appProperties.getUserId();
        if(response.storyLabel) {
          this.storyLabel = response.storyLabel
        }
        this.parseVoted(response.voted);
        this.parseVotes(response.votes);
        break;
      case 'VOTE' : 
        this.parseVoted(response.voted);
        this.parseVotes(response.votes);
        break;
      default:
        console.error('type de message non géré');
        break;
    }
  }

  parseVoted(voted: Array<string>) {
    this.aVote = false;
    for (const element of this.usersMap.values()) {
      element.voted = false;
    }
    if(voted) {
      voted.forEach(v => {
        if(this.appProperties.getUserId() === v){
          this.aVote = true;
        }
        this.usersMap.get(v).voted = true;
      });
    }
   
  }

  parseVotes(votes: any) {
    this.votesMap.clear();
    this.votesArray = undefined;
    for (const element of this.usersMap.values()) {
      element.vote = undefined;
    }

    if(votes) {
      const keys: string[] = Object.keys(votes);
      keys.forEach(k => {
        const user = this.usersMap.get(k);
        user.vote = +votes[k];
        if(this.votesMap.has(user.vote)){
          this.votesMap.get(user.vote).push(user);
        } else {
          const userArray = new Array<User>();
          userArray.push(user);
          this.votesMap.set(user.vote, userArray);
        }
      });
      this.votesArray = new Array<string>();
      for (const entry of this.votesMap.entries()) {
        this.votesArray.push(entry[0] + ' : ' + entry[1].map(x => x.displayName).join(', '));
      }
    }
  }

  mapUserArray(users: Array<User>): Array<User> {
    this.usersMap.clear();
    users.forEach(x => {
      x.voted = false;
      this.usersMap.set(x.name, x);
    });
    return users;
  }

  copyToClipboard() {
    let url = window.location.origin;
    if(environment.production){
      url += '/app';
    }
    this.clipboard.copy(url + '/room/' + this.roomId);
  }

  changerUS(libelleUS: string) {
    this.storyLabel = libelleUS;
    this.wsService.startNewStory(this.storyLabel);
  }

  voter(vote: number) {
    this.wsService.voter(vote);
    this.aVote = true;
  }

  revelerVotes() {
    this.wsService.revelerVotes();
  }

}
