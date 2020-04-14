import { ModuleDirective } from './module.directive';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { ModulesComponent } from './components/module-list/module-list.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './shared/authconfig.interceptor';
import { AppRoutingModule } from './app-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { SearchbarModule } from './components/searchbar/searchbar.module';
import { SqlInjectionTutorialComponent } from './components/sql-injection-tutorial/sql-injection-tutorial.component';
import { ModuleItemComponent } from './components/module-item/module-item.component';
import { XssTutorialComponent } from './components/xss-tutorial/xss-tutorial.component';

@NgModule({
  declarations: [
    AppComponent,
    SigninComponent,
    SignupComponent,
    ModulesComponent,
    SqlInjectionTutorialComponent,
    ModuleItemComponent,
    XssTutorialComponent,
    ModuleDirective
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    SearchbarModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
