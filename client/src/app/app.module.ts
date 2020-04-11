import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { ModulesComponent } from './components/modules/modules.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './shared/authconfig.interceptor';
import { AppRoutingModule } from './app-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { SearchbarModule } from './searchbar/searchbar.module';
import { SqlInjectionTutorialComponent } from './sql-injection-tutorial/sql-injection-tutorial.component';

@NgModule({
  declarations: [
    AppComponent,
    SigninComponent,
    SignupComponent,
    ModulesComponent,
    SqlInjectionTutorialComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    SearchbarModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
