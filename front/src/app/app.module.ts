import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { HeaderComponent } from './common/header/header.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { LoginComponent } from './common/login/login.component';
import { RoomComponent } from './room/room.component';
import { CookieService } from 'ngx-cookie-service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { JoinRoomComponent } from './room/join-room/join-room.component';
import { HttpClientModule } from '@angular/common/http';
import { PageNotFoundComponent } from './common/page-not-found/page-not-found.component';
import { ClipboardModule } from '@angular/cdk/clipboard';
import { MatListModule } from '@angular/material/list';
import { StoryCreatorComponent } from './room/story-creator/story-creator.component';
import { VoteInputComponent } from './room/vote-input/vote-input.component';
import { CardComponent } from './room/card/card.component';
import { ChartsModule } from 'ng2-charts';
import { ResultsChartComponent } from './room/results/results-chart/results-chart.component';
import { ResultsBoardComponent } from './room/results/results-board/results-board.component';
import { MatSnackBarModule } from '@angular/material/snack-bar'; 
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    RoomComponent,
    JoinRoomComponent,
    PageNotFoundComponent,
    StoryCreatorComponent,
    VoteInputComponent,
    CardComponent,
    ResultsChartComponent,
    ResultsBoardComponent,
  ],
  imports: [
    ChartsModule,
    ClipboardModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatToolbarModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatMenuModule,
    HttpClientModule,
    MatListModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  providers: [CookieService],
  bootstrap: [AppComponent],
})
export class AppModule {}
