import { Module } from './../../shared/module';
import { Component, OnInit } from '@angular/core';
import { AuthService } from './../../shared/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-modules',
  templateUrl: './modules.component.html',
  styleUrls: ['./modules.component.css'],
})
export class ModulesComponent implements OnInit {
  modules: Module[];

  constructor(public authService: AuthService, public router: Router) {
    this.modules = [];
  }
  ngOnInit(): void {
    this.authService.getModules().subscribe((modules: Module[]) => {
      this.modules = modules;
    });
  }
  getUrlById(id: number): string {
    return 'sql-injection-tutorial';
  }
}
