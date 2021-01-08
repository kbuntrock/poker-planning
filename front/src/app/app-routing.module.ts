import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { LoginComponent } from './common/login/login.component';
import { PageNotFoundComponent } from './common/page-not-found/page-not-found.component';
import { JoinRoomComponent } from './room/join-room/join-room.component';
import { RoomComponent } from './room/room.component';
import { AuthGuard } from './shared/auth.guard';

const LOGIN_URL = 'login';
const JOIN_ROOM_URL = 'join-room';
const ROOM_URL= 'room';

const routes: Routes = [
  { path: LOGIN_URL, component: LoginComponent },
  { path: JOIN_ROOM_URL, component: JoinRoomComponent, canActivate: [AuthGuard] },
  { path: ROOM_URL+'/:roomId', component: RoomComponent, canActivate: [AuthGuard] },
  { path: '',   redirectTo: '/join-room', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
