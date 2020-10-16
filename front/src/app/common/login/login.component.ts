import { Component, OnInit } from '@angular/core';
import { PropertiesService } from '../properties.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public loginForm: FormGroup;

  constructor(private readonly appProperties: PropertiesService,  private formBuilder: FormBuilder) {
    this.loginForm = this.formBuilder.group({
      username: ''
    });
   }

  ngOnInit(): void {
  }

  onSubmit(value: any) {
    this.appProperties.setUsername(value.username);
  }
}
