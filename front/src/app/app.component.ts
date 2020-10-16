import { Component } from '@angular/core';
import { PropertiesService } from './common/properties.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  public title = 'poker-planning';
  public hasUsername = false;
  public username = undefined;

  constructor(private readonly appProperties: PropertiesService) {
    console.info('user id : ' + this.appProperties.getUserId());
    console.info('user name : ' + this.appProperties.getUsername());
    
    this.hasUsername = this.appProperties.hasUsername();
    console.info('has username : ' + this.hasUsername );
    this.username = this.appProperties.getUsername();
  }
}
