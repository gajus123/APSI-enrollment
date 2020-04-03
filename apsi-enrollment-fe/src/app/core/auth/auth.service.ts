import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, of, Subject, throwError, timer } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { TokenBundle, UserCredentials } from '../model/auth.model';

const AUTH_TOKEN_BUNDLE_KEY = 'auth';

@Injectable({
  providedIn: 'root',
})
export class AuthService implements OnDestroy {
  private authBaseUrl: string;
  private tokenSubject$: BehaviorSubject<TokenBundle>;
  private subscriptions$: Subject<void>;

  authToken$: Observable<string | null>;
  isAuthenticated$: Observable<boolean>;

  constructor(private zone: NgZone, private http: HttpClient) {
    this.authBaseUrl = environment.authBaseUrl;
    this.tokenSubject$ = new BehaviorSubject<TokenBundle>(null);
    this.subscriptions$ = new Subject<void>();
    this.authToken$ = this.tokenSubject$.pipe(map((tokenBundle) => (tokenBundle ? tokenBundle.accessToken : null)));
    this.isAuthenticated$ = this.tokenSubject$.pipe(map(Boolean));

    const storedToken = this.loadTokenBundle();
    if (storedToken != null) {
      this.tokenSubject$.next(storedToken);
    }
  }

  ngOnDestroy() {
    this.subscriptions$.next();
    this.subscriptions$.complete();
  }

  signIn(username: string, password: string): Observable<boolean> {
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
      }),
      catchError((err) => {
        this.setTokenBundle(null);
        return throwError(err);
      }),
      tap((tokenBundle: TokenBundle) => this.setTokenBundle(tokenBundle)),
      map(() => true)
    );
  }

  signOut(): Observable<void> {
    return of(null).pipe(tap(() => this.setTokenBundle(null)));
  }

  refreshToken(): Observable<string> {
    return this.http.post(`${this.authBaseUrl}/refresh`, { refreshToken: this.tokenSubject$.value.refreshToken }).pipe(
      map((response: TokenBundle) => {
        if (response == null || response.accessToken == null || response.refreshToken == null) {
          return null;
        }
        return response;
      }),
      tap((tokenBundle) => this.setTokenBundle(tokenBundle)),
      map((tokenBundle) => tokenBundle.accessToken)
    );
  }

  private setTokenBundle(bundle: TokenBundle) {
    this.tokenSubject$.next(bundle);
    this.storeTokenBundle(bundle);
  }

  private storeTokenBundle(bundle: TokenBundle) {
    if (bundle != null) {
      const serializedBundle = JSON.stringify(bundle);
      window.localStorage.setItem(AUTH_TOKEN_BUNDLE_KEY, serializedBundle);
    } else {
      window.localStorage.removeItem(AUTH_TOKEN_BUNDLE_KEY);
    }
  }

  private loadTokenBundle(): TokenBundle | null {
    const serializedBundle = window.localStorage.getItem(AUTH_TOKEN_BUNDLE_KEY);
    if (serializedBundle == null) {
      return null;
    }
    try {
      const bundle = JSON.parse(serializedBundle);
      return bundle;
    } catch (e) {
      console.error('error parsing stored token bundle: ' + e);
      return null;
    }
  }
}
