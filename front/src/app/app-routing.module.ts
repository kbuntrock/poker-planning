import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { LoginComponent } from './common/login/login.component';
import { CreateRoomComponent } from './room/create-room/create-room.component';
import { JoinRoomComponent } from './room/join-room/join-room.component';
import { RoomComponent } from './room/room.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'join-room', component: JoinRoomComponent },
  { path: 'room/:roomId', component: RoomComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
