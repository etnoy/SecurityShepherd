import { Component } from '@angular/core';
import { AuthService } from './shared/auth.service';
import { Module } from './shared/module';
import { Observable } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  map,
  filter,
} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  constructor(public authService: AuthService) {}

  logout() {
    this.authService.doLogout();
  }
}
