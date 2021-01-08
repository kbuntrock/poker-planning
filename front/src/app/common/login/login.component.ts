import { Component, OnInit } from '@angular/core';
import { PropertiesService } from '../properties.service';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { WebsocketService } from '../../../shared/websocket.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public loginForm: FormGroup;

  constructor(
    private readonly appProperties: PropertiesService, 
    private formBuilder: FormBuilder, 
    private wsService: WebsocketService, 
    private router: Router) {

    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.maxLength(15)]]
    });
   }

  ngOnInit(): void {
  }

  onSubmit(value: any) {
    if(this.loginForm.valid) {
      this.wsService.connect();
      this.appProperties.setUsername(value.username);
      this.router.navigate(['join-room']);
    }
  }
}
