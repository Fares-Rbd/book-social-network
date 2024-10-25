import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import KeycloakService from "../keycloak/keycloak.service";

@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {

  constructor(
    private KeycloakService: KeycloakService
  ) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    const token = this.KeycloakService.keycloak.token;
    if (token) {
      const authReq = request.clone({
        headers: new HttpHeaders({
          Authorization: 'Bearer ' + token
        })
      });
      return next.handle(authReq)
    }

    return next.handle(request);
  }
}
