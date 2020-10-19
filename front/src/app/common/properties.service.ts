import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import * as uuid from 'uuid';
import { Subject, BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PropertiesService {

  private static readonly USER_ID_COOKIE_NAME = 'userId';
  private static readonly USERNAME_COOKIE_NAME = 'username';

  private userId: string;
  private username$ = new BehaviorSubject<string>(undefined);

  constructor(private cookieService: CookieService) {
    if (this.cookieService.check(PropertiesService.USER_ID_COOKIE_NAME)) {
      this.userId = this.cookieService.get(PropertiesService.USER_ID_COOKIE_NAME);
    }
    if (this.cookieService.check(PropertiesService.USERNAME_COOKIE_NAME)) {
      this.username$.next(this.cookieService.get(PropertiesService.USERNAME_COOKIE_NAME));
    }
    if (!this.userId) {
      this.userId = uuid.v4();
      this.cookieService.set(PropertiesService.USER_ID_COOKIE_NAME, this.userId);
    }
  }

  public getUserId(): string {
    return this.userId;
  }

  public eraseUsername(): void {
    this.username$.next(undefined);
    this.cookieService.delete(PropertiesService.USERNAME_COOKIE_NAME);
  }

  public getUsername$(): Observable<string> {
    return this.username$;
  }

  public getUsername(): string {
    return this.username$.getValue();
  }

  public setUsername(username: string): void {
    this.username$.next(username);
    this.cookieService.set(PropertiesService.USERNAME_COOKIE_NAME, username);
  }

  public hasUsername(): boolean {
    return this.username$ !== undefined;
  }
}
