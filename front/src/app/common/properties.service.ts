import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import * as uuid from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class PropertiesService {

  private static readonly USER_ID_COOKIE_NAME = 'userId';
  private static readonly USERNAME_COOKIE_NAME = 'username';

  private userId: string;
  private username: string;

  constructor(private cookieService: CookieService) { 
    this.userId = this.cookieService.get(PropertiesService.USER_ID_COOKIE_NAME);
    this.username = this.cookieService.get(PropertiesService.USERNAME_COOKIE_NAME);
    if (!this.userId) {
      this.userId = uuid.v4();
      this.cookieService.set(PropertiesService.USER_ID_COOKIE_NAME, this.userId);
    }
  }

  public getUserId(): string {
    return this.userId;
  }

  public eraseUsername(): void {
    this.username = undefined;
    this.cookieService.delete(PropertiesService.USERNAME_COOKIE_NAME);
  }

  public getUsername(): string {
    return this.username;
  }

  public setUsername(username: string): void {
    this.username = username;
    this.cookieService.set(PropertiesService.USERNAME_COOKIE_NAME, this.username);
  }

  public hasUsername(): boolean {
    return this.username !== undefined;
  }
}
