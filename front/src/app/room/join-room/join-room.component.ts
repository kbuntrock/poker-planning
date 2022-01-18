import { PlatformLocation } from '@angular/common'
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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

  public spForm: FormGroup;

  private url: string

  constructor(private http: HttpClient, private wsService: WebsocketService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly platformLocation: PlatformLocation,
    private readonly formBuilder: FormBuilder) {

    this.url = window.location.origin + platformLocation.getBaseHrefFromDOM() + '../api';

    this.spForm = this.formBuilder.group({
      roomName: ['', [Validators.required, Validators.maxLength(35)]]
    });
  }

  ngOnInit(): void {
  }

  public async createRoom() {
    if(this.spForm.valid) {
      const room = await this.http.post<Room>(this.url + '/planning/create', this.spForm.get('roomName').value, { withCredentials: true }).toPromise();
      this.router.navigate(['room/'+room.planningUuid], { relativeTo: this.route.parent });
    }
  }

}
