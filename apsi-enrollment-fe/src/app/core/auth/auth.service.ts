import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { TokenBundle } from '../model/auth.model';
import { AuthAPIService } from './auth-api.service';

const AUTH_TOKEN_BUNDLE_KEY = 'auth';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authTokenSubject$: BehaviorSubject<TokenBundle>;

  authToken$: Observable<string | null>;
  isAuthenticated$: Observable<boolean>;

  constructor(private authAPI: AuthAPIService) {
    this.authTokenSubject$ = new BehaviorSubject<TokenBundle>(null);

    this.authToken$ = this.authTokenSubject$.pipe(map((tokenBundle) => (tokenBundle ? tokenBundle.accessToken : null)));
    this.isAuthenticated$ = this.authTokenSubject$.pipe(map(Boolean));

    const storedToken = this.loadTokenBundle();
    if (storedToken != null) {
      this.authTokenSubject$.next(storedToken);
    }
  }

  signIn(username: string, password: string): Observable<boolean> {
    return this.authAPI.signIn(username, password).pipe(
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
    return this.authAPI.refresh(this.authTokenSubject$.value.refreshToken).pipe(
      tap((tokenBundle) => this.setTokenBundle(tokenBundle)),
      map((tokenBundle) => tokenBundle.accessToken)
    );
  }

  private setTokenBundle(bundle: TokenBundle) {
    this.authTokenSubject$.next(bundle);
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
