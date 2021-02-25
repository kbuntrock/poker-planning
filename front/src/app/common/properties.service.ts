import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import * as uuid from 'uuid';
import { Subject, BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PropertiesService {

  private static readonly USER_ID_COOKIE_NAME = 'userId';
  private static readonly USERKEY_COOKIE_NAME = 'userKey';
  private static readonly USERNAME_COOKIE_NAME = 'username';
  private static readonly COOKIE_MAX_AGE: number = 60; // days

  private userId: string;
  private userKey: string;
  private username$ = new BehaviorSubject<string>(undefined);

  constructor(private cookieService: CookieService) {
    if (this.cookieService.check(PropertiesService.USER_ID_COOKIE_NAME)) {
      this.userId = this.cookieService.get(PropertiesService.USER_ID_COOKIE_NAME);
    }
    if (this.cookieService.check(PropertiesService.USERKEY_COOKIE_NAME)) {
      this.userKey = this.cookieService.get(PropertiesService.USERKEY_COOKIE_NAME);
    }
    if (this.cookieService.check(PropertiesService.USERNAME_COOKIE_NAME)) {
      this.username$.next(this.cookieService.get(PropertiesService.USERNAME_COOKIE_NAME));
    }

    this.initIdIfNeeded();
  }

  public getUserId(): string {
    return this.userId;
  }

  public disconnect(): void {
    this.userId = undefined;
    this.userKey = undefined;
    this.username$.next(undefined);
    this.cookieService.delete(PropertiesService.USER_ID_COOKIE_NAME, '/');
    this.cookieService.delete(PropertiesService.USERKEY_COOKIE_NAME, '/');
    this.cookieService.delete(PropertiesService.USERNAME_COOKIE_NAME, '/');
  }

  public getUsername$(): Observable<string> {
    return this.username$;
  }

  public getUsername(): string {
    return this.username$.getValue();
  }

  public setUsername(username: string): void {
    this.username$.next(username);
    this.initIdIfNeeded();
  }

  public hasUsername(): boolean {
    return this.username$ !== undefined;
  }

  private initIdIfNeeded(): void {
      if (!this.userId) {
        this.userId = uuid.v4();
      }
      if (!this.userKey) {
        this.userKey = uuid.v4();
      }

      // always set cookies to reset max-age
      this.cookieService.set(PropertiesService.USER_ID_COOKIE_NAME, this.userId, PropertiesService.COOKIE_MAX_AGE, '/');
      this.cookieService.set(PropertiesService.USERKEY_COOKIE_NAME, this.userKey, PropertiesService.COOKIE_MAX_AGE, '/');
      this.cookieService.set(PropertiesService.USERNAME_COOKIE_NAME, this.username$.getValue(), PropertiesService.COOKIE_MAX_AGE, '/');
  }
}
