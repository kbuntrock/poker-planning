import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
    const room = await this.http.get<Room>('http://localhost:8080/planning/create', { withCredentials: true }).toPromise();
    this.router.navigate(['room/'+room.planningUuid], { relativeTo: this.route.parent });
  }

}
