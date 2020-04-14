import { ModuleItemComponent } from './module-item/module-item.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { ModulesComponent } from './components/modules/modules.component';

import { AuthGuard } from './shared/auth.guard';
import { SqlInjectionTutorialComponent } from './sql-injection-tutorial/sql-injection-tutorial.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: SigninComponent },
  { path: 'register', component: SignupComponent },
  { path: 'modules', component: ModulesComponent, canActivate: [AuthGuard] },
  {
    path: 'module/:id',
    component: ModuleItemComponent,
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
