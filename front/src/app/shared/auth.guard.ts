import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router, Params } from '@angular/router';
import { Observable } from 'rxjs';
import { PropertiesService } from '../common/properties.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private readonly propertiesService: PropertiesService, private router: Router){
    
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if(this.propertiesService.getUsername()){
      return true;
    }
    let queryParams: Params = undefined;
    if(next.url.length === 2 && next.url[0].path === 'room'){
      queryParams = { roomId: next.url[1].path };
    }
    this.router.navigate(['login'], {
      queryParams: queryParams
    });
    return false;
  }
  
}
