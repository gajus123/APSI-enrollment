import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { switchMap, takeUntil } from 'rxjs/operators';
import { User } from '../model/user.model';
import { AuthAPIService } from './auth-api.service';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class CurrentUserService implements OnDestroy {
  private currentUserSubject$: BehaviorSubject<User>;
  private subscriptions$: Subject<void>;

  currentUser$: Observable<User>;

  constructor(auth: AuthService, authAPI: AuthAPIService) {
    this.subscriptions$ = new Subject<void>();
    this.currentUserSubject$ = new BehaviorSubject<User>(null);
    this.currentUser$ = this.currentUserSubject$.pipe();

    auth.authToken$
      .pipe(
        switchMap((token) => authAPI.getUserForToken(token)),
        takeUntil(this.subscriptions$)
      )
      .subscribe((user) => this.currentUserSubject$.next(user));
  }

  ngOnDestroy() {
    this.subscriptions$.next();
    this.subscriptions$.complete();
  }
}
