import { PlatformLocation } from '@angular/common'
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Client, IFrame, messageCallbackType, Stomp, StompSubscription } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { PropertiesService } from '../app/common/properties.service';
import { environment } from '../environments/environment';

export enum SocketClientState {
  ATTEMPTING_CONNECTION, CONNECTED, ATTEMPTING_DISCONNECTION, DISCONNECTED
}

export interface User {
  name: string,
  displayName: string,
  voted: boolean,
  vote: number,
  connected: boolean
}

export interface WSMessage {
  type: string,
  connectedUsers: Array<User>,
  adminList: Array<string>,
  storyLabel: string,
  voted: Array<string>,
  votes: Array<string>
}

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private client: Client;
  private roomId: string = undefined;

  private subscriptions: StompSubscription[] = new Array<StompSubscription>();

  private state: BehaviorSubject<SocketClientState> =  new BehaviorSubject<SocketClientState>(SocketClientState.DISCONNECTED);

  constructor( private readonly appProperties: PropertiesService, private readonly platformLocation: PlatformLocation) {

    // Important : Il faut passer une fonction capable de renvoyer une nouvelle instance du websocket sous peine de ne pas avoir la reconnection automatique
    // (et de ne pas pouvoir alterner les activate/deactivate)

    console.log("environment.production");
    console.log(environment.production);
    let url = 'http://localhost:8080/' + platformLocation.getBaseHrefFromDOM() + '../websocket';
    if(environment.production){
      url = window.location.origin + platformLocation.getBaseHrefFromDOM() + '../websocket'
    }
    console.log(url);

    this.client = Stomp.over(() => new SockJS(url, null, {transports:['websocket','eventsource','xhr-polling']}));

    this.client.onConnect = this.onConnect;
    this.client.onDisconnect = this.onDisconnect;

    this.client.reconnectDelay = 500;
    this.client.heartbeatIncoming = 20000;
    this.client.heartbeatOutgoing = 20000;
  }

  public getClientState(): SocketClientState {
    return this.state.getValue();
  }

  public getClientState$(): BehaviorSubject<SocketClientState> {
    return this.state;
  }

  connect() {
    console.info("Connexion ...")
    this.state.next(SocketClientState.ATTEMPTING_CONNECTION);
    this.client.activate();
  }

  disconnect() {
    if(this.client.connected){
      console.info("Déconnexion ...")
      this.state.next(SocketClientState.ATTEMPTING_DISCONNECTION);
      this.client.deactivate();
      this.roomId = undefined;
    }
  }

  private onConnect = (receipt: IFrame) => {
    // Do something, all subscribes must be done is this callback
      // This is needed because this will be executed after a (re)connect
      console.info("On est connecté!!")
      this.state.next(SocketClientState.CONNECTED);

      // Souscription aux erreurs
      this.subscriptions.push(this.client.subscribe('/user/topic/error', function (errorText) {
        console.error('topic error : ' + errorText);
      }));

  }

  private onDisconnect = (receipt: IFrame) => {
    this.state.next(SocketClientState.DISCONNECTED);
    this.subscriptions.forEach(s => {
      s.unsubscribe();
    });
  }

  public joinRoom(roomId: string, callback: messageCallbackType){
    this.roomId = roomId;
    // On souscrit à la room
    this.subscriptions.push(
      this.client.subscribe('/topic/planning/'+roomId, callback)
    );
    this.subscriptions.push(
      this.client.subscribe('/user/topic/planning/'+roomId, callback)
    );
    // et on s'enregistre dessus en tant qu'utilisateur
    console.info(JSON.stringify({'displayName': this.appProperties.getUsername(), 'name':this.appProperties.getUserId()}));
    this.client.publish({
      destination: '/app/planning/'+roomId+'/register',
      body: JSON.stringify({'displayName': this.appProperties.getUsername(), 'name':this.appProperties.getUserId()})
    });
  }

  public startNewStory(storyName: string){
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/newStory',
      body: storyName
    });

  }

  public voter(vote: number) {
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/vote',
      body: JSON.stringify({'value': vote})
    });

  }

  public revelerVotes() {
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/reveal'
    });

  }
}
