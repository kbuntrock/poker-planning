import { PlatformLocation } from '@angular/common'
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { WebsocketService } from '../../../shared/websocket.service';
import { Room } from '../room';

@Component({
  selector: 'app-join-room',
  templateUrl: './join-room.component.html',
  styleUrls: ['./join-room.component.scss']
})
export class JoinRoomComponent implements OnInit {
  private url: string

  constructor(private http: HttpClient, private wsService: WebsocketService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly platformLocation: PlatformLocation) {

    if(environment.production){
      this.url = window.location.origin + platformLocation.getBaseHrefFromDOM() + '..'
    } else {
      this.url = 'http://localhost:8080/' + platformLocation.getBaseHrefFromDOM() + '..';
    }
  }

  ngOnInit(): void {
  }

  public async createRoom() {
    const room = await this.http.get<Room>(this.url + '/planning/create', { withCredentials: true }).toPromise();
    this.router.navigate(['room/'+room.planningUuid], { relativeTo: this.route.parent });
  }

}
