import { Component, OnInit, OnDestroy } from '@angular/core';
import { PropertiesService } from './common/properties.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  public title = 'poker-planning';
  public username: string = undefined;

  private subscriptions = new Subscription();

  constructor(private readonly appProperties: PropertiesService) {
    console.info('user id : ' + this.appProperties.getUserId());
  }

  ngOnInit(): void {
    this.subscriptions.add(this.appProperties.getUsername$().subscribe(username => {
      console.info('username : ' + username);
      this.username = username;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  
}
