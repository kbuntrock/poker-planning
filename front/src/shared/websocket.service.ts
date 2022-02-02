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
  connected: boolean,
  spectator: boolean
}

export interface WSMessage {
  type: string,
  voteValues: Array<number>,
  connectedUsers: Array<User>,
  adminList: Array<string>,
  storyLabel: string,
  voted: Array<string>,
  myVote: number,
  votes: Array<string>,
  roomName: string
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

    let url = window.location.origin + platformLocation.getBaseHrefFromDOM() + '../api/websocket'

    // Important : Il faut passer une fonction capable de renvoyer une nouvelle instance du websocket sous peine de ne pas avoir la reconnection automatique
    // (et de ne pas pouvoir alterner les activate/deactivate)
    this.client = Stomp.over(() => new SockJS(url, null, 
      {
        transports:['websocket','eventsource','xhr-polling'],
        sessionId: 15
      }));

    this.client.onConnect = this.onConnect;
    this.client.onDisconnect = this.onDisconnect;
    this.client.onWebSocketClose = this.onWebSocketClose;
    this.client.onStompError = this.onStompError;

    this.client.reconnectDelay = 5000;
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
    this.client.connectHeaders = {
      'userId': this.appProperties.getUserId(),
      'username': this.appProperties.getUsername(),
      'userKey': this.appProperties.getUserKey()
    };

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
        console.error(errorText);
      }));

  }

  private onDisconnect = (receipt: IFrame) => {
    console.info('onDisconnect called')
    this.disconnectedHandler();
  }

  private readonly onWebSocketClose = (event: any) => {
    console.info('onWebsocketClose called')
    this.disconnectedHandler();
  };

  private disconnectedHandler() {
    this.state.next(SocketClientState.DISCONNECTED);
    this.subscriptions.forEach(s => {
      s.unsubscribe();
    });
  }

  /**
   * Si la déconnexion est innopinée, ce n'est pas la callback "onDisconnect" qui est appelée directement.
   * Mais on l'appelle en chaîne ici.
   * @param receipt 
   */
  private onStompError = (receipt: IFrame) => {
    console.info('StompError happened')
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
    // Cette dernière souscription permet d'initialiser les données mais elle ne sera pas sauvegardée côté back
    // https://docs.spring.io/spring-framework/docs/5.3.9/reference/html/web.html#websocket-stomp-subscribe-mapping
    this.subscriptions.push(
      this.client.subscribe('/app/planning/'+roomId, callback)
    );
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

  public revoter() {
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/revote'
    });

  }

  promoteUser(userId: string){
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/promote-user',
      body: userId
    });
  }

  demoteUser(userId: string){
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/demote-user',
      body: userId
    });
  }

  setSpectator(userId: string, isSpectator: boolean){
    this.client.publish({
      destination: '/app/planning/'+this.roomId+'/'+userId+'/set-spectator',
      body: '' + isSpectator
    });
  }
}
