import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private isTokenRefreshInProgress: boolean;
  private tokenRefreshCompletedSubject$: BehaviorSubject<boolean>;

  constructor(private authService: AuthService) {
    this.isTokenRefreshInProgress = false;
    this.tokenRefreshCompletedSubject$ = new BehaviorSubject(true);
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('interceptor url', req.url);
    if (req.url.startsWith('assets') || req.url.startsWith(environment.authBaseUrl)) {
      return next.handle(req);
    }

    return this.injectToken(req).pipe(
      switchMap((request) => next.handle(request)),
      catchError((err) => {
        if (err instanceof HttpErrorResponse && err.status === 401) {
          if (this.isTokenRefreshInProgress) {
            return this.tokenRefreshCompletedSubject$.pipe(filter((completed) => completed === true));
          } else {
            this.tokenRefreshCompletedSubject$.next(false);
            return this.authService.refreshToken().pipe(
              catchError((refreshError) => {
                console.error('error while refreshing token from interceptor: ', refreshError);
                return throwError(err);
              }),
              tap(() => {
                this.isTokenRefreshInProgress = false;
                this.tokenRefreshCompletedSubject$.next(true);
              })
            );
          }
        } else {
          return throwError(err);
        }
      }),
      switchMap(() => {
        return this.injectToken(req);
      }),
      switchMap((request) => next.handle(request))
    );
  }

  private injectToken(req: HttpRequest<any>): Observable<HttpRequest<any>> {
    return this.authService.authToken$.pipe(
      first(),
      map((token) => {
        let headers = {};
        if (token) {
          headers = { Authorization: `Bearer ${token}` };
        }
        const request = req.clone({ setHeaders: headers });
        return request;
      })
    );
  }
}
