import { bootstrapApplication } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { provideRouter } from "@angular/router";
import { routes } from "./app/app-routing.module";
import { provideHttpClient, withInterceptors } from "@angular/common/http"
import { authInterceptor } from "./app/intereptors/auth.interceptor";
import { AuthService } from "./app/services/auth.service";
import { APP_INITIALIZER } from "@angular/core";

function initializeAuth(auth: AuthService){
  return () => auth.initAuth()
}

bootstrapApplication(AppComponent, {providers: [
  provideRouter(routes),
  provideHttpClient(withInterceptors([authInterceptor])),
  {
    provide: APP_INITIALIZER,
    useFactory: initializeAuth,
    deps: [AuthService],
    multi: true
  }
]})