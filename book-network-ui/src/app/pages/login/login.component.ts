import {Component, OnInit} from '@angular/core';
import {AuthenticationRequest} from "../../services/models/authentication-request";
import KeycloakService from "../../services/keycloak/keycloak.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(private keycloakService: KeycloakService) {
  }

  async ngOnInit() {
    await this.keycloakService.init();
    await this.keycloakService.login();
  }
  
  // login() {
  //   this.errorMsg = []; // this is needed to remove any error messages that could have been generated in previous login attempts
  //   this.authService.authenticate({
  //     body: this.authRequest
  //   }).subscribe({
  //     next: (res): void => {
  //       this.tokenService.token = res.token as string;
  //       this.router.navigate(['books']);
  //     },
  //     error: (err): void => {
  //       console.log(err);
  //       if (err.error.validationErrors) {
  //         this.errorMsg = err.error.validationErrors
  //       } else {
  //         this.errorMsg.push(err.error.error);
  //       }
  //     }
  //   })
  // }
  //
  // register() {
  //   this.router.navigate(['register']);
  // }
}
