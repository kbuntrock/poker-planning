import { Component, OnDestroy, OnInit, NgZone } from '@angular/core';
import { PlatformLocation } from '@angular/common'
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SocketClientState, User, WebsocketService, WSMessage } from '../../shared/websocket.service';
import { Clipboard } from '@angular/cdk/clipboard';
import { PropertiesService } from '../common/properties.service';
import { environment } from '../../environments/environment';
import { VoteValue } from '../model/vote-value';
import { MatSnackBar } from '@angular/material/snack-bar';

enum ColorScheme {
  Green = "Green",
  GreenToRed = "Green to red",
  Rainbow = "Rainbow",
  BlueGradient = "Blue Gradient",
  BlueGradient2 = "Blue Gradient 2"
}

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss']
})
export class RoomComponent implements OnInit, OnDestroy {

  roomId: string;
  voteValues: Array<VoteValue>;
  colorScheme: ColorScheme = ColorScheme.GreenToRed;

  // Indique si l'utilisateur peut proposer des storys
  isScrumMaster: boolean = false;
  creatingNewRoom: boolean = false;
  voteInProgress: boolean = false;
  atLeastOnePersonVoted: boolean = false;
  myVote: number;

  users: Array<User>;
  usersMap = new Map<string, User>();
  adminList: Array<string> = undefined;
  storyLabel: string = "..."

  votesMap = new Map<number, Array<User>>();
  votesArray: Array<string> = undefined;

  subscription: Subscription = new Subscription();

  constructor(private readonly route: ActivatedRoute, private readonly wsService: WebsocketService,
    private readonly appProperties: PropertiesService,
    private readonly ngZone: NgZone,
    private readonly platformLocation: PlatformLocation,
    private snackBar: MatSnackBar)  { }

  ngOnInit(): void {
    this.roomId = this.route.snapshot.params['roomId'];
    if(this.wsService.getClientState() !== SocketClientState.CONNECTED){
      // La connexion est en cours, on rejoindra la room quand elle sera effective
      this.subscription.add(this.wsService.getClientState$().subscribe(state => {
        if(state === SocketClientState.CONNECTED){
          this.joinRoom();
        }
      }));
    } else {
      // On est déjà connecté, on rejoint la room directement
      this.joinRoom();
    }

  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private joinRoom() {
    this.wsService.joinRoom(this.roomId, (message) => {
      this.ngZone.run( () => { // when using serverSentEvent, changes are not in angular zone and become unreactive
        this.onPlanningMessage(message.body);
      });
    });
  }

  onPlanningMessage(message: string) {
    const response: WSMessage = JSON.parse(message);
    switch(response.type) {
      case 'FULL' :
        this.parseFull(response);
        break;
      case 'STATE' :
        this.parseState(response);
        break;
      case 'VOTE' :
        this.parseVoted(response.voted);
        this.parseVotes(response.votes);
        break;
      default:
        console.error('type de message non géré');
        break;
    }
    if (response.myVote) this.myVote = response.myVote;
  }

  parseFull(response: WSMessage) {
    this.voteValues = [];
    response.voteValues.forEach(v => {
      this.voteValues.push(new VoteValue(v));
    });
    this.appProperties.getRoomInfos$().next(
    {
      name: response.roomName,
      url: window.location.origin + this.platformLocation.getBaseHrefFromDOM() + 'room/' + this.roomId
    });
    this.computeVoteValuesColors();
    this.parseState(response);
  }

  computeVoteValuesColors() {
    this.voteValues.forEach((v, idx) => {
      const percentage: number = idx / (this.voteValues.length - 1);
      switch (this.colorScheme) {
        case ColorScheme.Rainbow:
          v.setColor(percentage, 120, 360, 70, 80, 90, 90);
        break;
        case ColorScheme.GreenToRed:
          v.setColor(percentage, 120, 0, 70, 80, 90, 90);
        break;
        case ColorScheme.BlueGradient:
          v.setColor(percentage, 200, 220, 10, 100, 100, 75);
        break;
        case ColorScheme.BlueGradient2:
          v.setColor(percentage, 180, 250, 30, 70, 100, 90);
        break;
        case ColorScheme.Green:
          v.red = 105;
          v.green = 179;
          v.blue = 108;
        break;
      }
    });
  }

  parseState(response: WSMessage) {
    this.users = this.mapUserArray(response.connectedUsers);
    this.adminList = response.adminList;
    this.isScrumMaster = this.adminList.includes(this.appProperties.getUserId());
    if(response.storyLabel) {
      this.storyLabel = response.storyLabel
    }
    this.parseVoted(response.voted);
    this.parseVotes(response.votes);
  }

  parseVoted(voted: Array<string>) {
    this.atLeastOnePersonVoted = false;
    let aVote = false;
    for (const element of this.usersMap.values()) {
      element.voted = false;
    }
    if(voted) {
      if(voted.length > 0) {
        this.atLeastOnePersonVoted = true;
      }
      voted.forEach(v => {
        if(this.appProperties.getUserId() === v){
          aVote = true;
        }
        this.usersMap.get(v).voted = true;
      });

      if (!aVote) this.myVote = undefined;
    }

  }

  parseVotes(votes: any) {
    this.votesMap.clear();
    this.votesArray = undefined;
    for (const element of this.usersMap.values()) {
      element.vote = undefined;
    }
    this.voteInProgress = !votes;

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

  changerUS(libelleUS: string) {
    this.storyLabel = libelleUS;
    this.wsService.startNewStory(this.storyLabel);
  }

  voter(vote: number) {
    this.wsService.voter(vote);
    this.myVote = vote;
  }

  revelerVotes() {
    if(this.atLeastOnePersonVoted){
      this.wsService.revelerVotes();
    } else {
      this.snackBar.open("Il n'y a encore aucun vote à révéler!", undefined, { duration: 1500 });
    }
  }

}
