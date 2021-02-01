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

  constructor(private http: HttpClient, private wsService: WebsocketService,
    private readonly router: Router, 
    private readonly route: ActivatedRoute) { }

  ngOnInit(): void {
  }

  public async createRoom() {

    let url = 'http://localhost:8080';
    if(environment.production){
      url = window.location.origin
    }

    const room = await this.http.get<Room>(url + '/planning/create', { withCredentials: true }).toPromise();
    this.router.navigate(['room/'+room.planningUuid], { relativeTo: this.route.parent });
  }

}
