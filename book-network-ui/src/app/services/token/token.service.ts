import {Injectable} from '@angular/core';
import {JwtHelperService} from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private jwtHelper = new JwtHelperService();

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token() {
    return localStorage.getItem('token') as string;
  }

  isTokenNotValid() {
    return !this.isTokenValid();
  }

  private isTokenValid() {
    const token = this.token;
    if (!token) {
      return false;
    }

    const isTokenExpired = this.jwtHelper.isTokenExpired(token);
    if (isTokenExpired) {
      localStorage.clear();
      return false;
    }

    return true;
  }

  getUsernameFromToken(): string | null {
    const token = this.token;
    if (!token) {
      console.log("no token");
      return null;
    }
    console.log("token");
    const decodedToken = this.jwtHelper.decodeToken(token);
    return decodedToken ? decodedToken['fullName'] || null : null;
  }
}
