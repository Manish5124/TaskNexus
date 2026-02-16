import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, tap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, RegisterResponse } from '../models/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = environment.apiBaseUrl + '/api/auth'
  private userSubject = new BehaviorSubject<AuthResponse | null> (null)
  private accessToken: string | null =  null

  constructor(private http: HttpClient) { }

  initAuth(): Promise<void> {
    return new Promise((resolve) => {
      this.refreshToken().subscribe({
        next: () => resolve(),
        error: () => {
          this.accessToken = null;
          this.userSubject.next(null);
          resolve();
        }
      })
    })
  }

  login(req: LoginRequest) {
    return this.http.post<AuthResponse>(this.authApiUrl+"/login", req, {
      withCredentials: true
    }).pipe(
      tap(res => this.setAccessToken(res))
    )
  }

  register(req: RegisterRequest){
    return this.http.post<RegisterResponse>(this.authApiUrl+"/register", req)
  }

  refreshToken() {
    return this.http.post<AuthResponse>(this.authApiUrl+"/refresh", 
                                            {}, {withCredentials: true})
                                            .pipe(
                                              tap(res => this.setAccessToken(res))
                                            )
  }

  logout() {
    return this.http.post(this.authApiUrl+"/logout", {}, {withCredentials: true})
                        .pipe(
                          tap(()=> {
                            this.accessToken = null;
                            this.userSubject.next(null)
                          })
                        )
  }

  setAccessToken(auth: AuthResponse){
    this.accessToken = auth.accessToken
    this.userSubject.next(auth)
  }

  get token() {
    return this.accessToken;
  }

  hasRole(role: string): boolean {
    return this.userSubject.value?.role.includes(role) ?? false
  }

  isLoggedIn(): boolean {
    return this.accessToken ? true: false
  }
}
