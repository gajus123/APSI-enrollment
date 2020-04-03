import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { TokenBundle, UserCredentials } from '../model/auth.model';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthAPIService {
  private authBaseUrl: string;

  constructor(private http: HttpClient) {
    this.authBaseUrl = environment.authBaseUrl;
  }

  signIn(username: string, password: string): Observable<TokenBundle> {
    const credentials: UserCredentials = {
      username,
      password,
    };

    return this.http.post(`${this.authBaseUrl}/authenticate`, credentials).pipe(
      map((response: TokenBundle) => {
        if (response == null || response.accessToken == null || response.refreshToken == null) {
          return null;
        }
        return response;
      })
    );
  }
  refresh(refreshToken: string): Observable<TokenBundle> {
    return this.http.post(`${this.authBaseUrl}/refresh`, { refreshToken }).pipe(
      map((response: TokenBundle) => {
        if (response == null || response.accessToken == null || response.refreshToken == null) {
          return null;
        }
        return response;
      })
    );
  }
  getUserForToken(accessToken: string): Observable<User> {
    return this.http
      .get(`${this.authBaseUrl}/me`, { headers: new HttpHeaders({ Authorization: `Bearer ${accessToken}` }) })
      .pipe(map((user: User) => user));
  }
}
