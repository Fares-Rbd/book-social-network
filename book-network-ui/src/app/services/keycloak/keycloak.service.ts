import {Injectable} from '@angular/core';
import Keycloak from "keycloak-js";
import {UserProfile} from "./user-profile";

@Injectable({
  providedIn: 'root',
})
export default class KeycloakService {

  private _keycloak: Keycloak | undefined;
  private _profile: UserProfile | undefined //if we are unable to authenticate the user

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'book-social-network',
        clientId: 'bsn'
      })
    }
    return this._keycloak
  }

  constructor() {
  }

  async init() {
    console.log('Authenticating user...');
    const authenticated = await this.keycloak?.init({
      onLoad: 'login-required'
    })

    if (authenticated) {
      console.log('User Authenticated!');
      this._profile = (await this.keycloak?.loadUserProfile()) as UserProfile;
      this._profile.token = this.keycloak?.token;
    }
  }

  login() {
    return this.keycloak?.login()
  }

  logout() {
    return this.keycloak?.logout({redirectUri: 'http://localhost:4200'})
  }
}
