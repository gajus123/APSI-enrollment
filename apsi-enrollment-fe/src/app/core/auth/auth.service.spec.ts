import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { take } from 'rxjs/operators';
import { TestScheduler } from 'rxjs/testing';
import { AuthAPIService } from './auth-api.service';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let scheduler: TestScheduler;
  const authAPIMock = jasmine.createSpyObj('AuthAPIService', ['signIn', 'refresh']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        AuthService,
        {
          provide: AuthAPIService,
          useValue: authAPIMock,
        },
      ],
    });
    scheduler = new TestScheduler((actual, expected) => {
      expect(actual).toEqual(expected);
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should authenticate when successfully retrieved token', () => {
    authAPIMock.signIn
      .withArgs('test', 'testpass')
      .and.returnValue(of({ accessToken: 'authToken', refreshToken: 'refresh', tokenType: 'Bearer' }));
    service.signIn('test', 'testpass').subscribe();

    scheduler.run(({ expectObservable }) => {
      expectObservable(service.isAuthenticated$).toBe('a', { a: true });
    });
  }, 2000);

  it('should not authenticate when cannot retrieve token', () => {
    authAPIMock.signIn.withArgs('test', 'testpass').and.returnValue(throwError({ status: 400 }));
    service.signIn('test', 'testpass').subscribe(
      () => {},
      (err) => {
        expect(err.status).toEqual(400);
      },
      () => {}
    );
  });

  it('should remove token on sign out', () => {
    service.signOut().subscribe();

    scheduler.run(({ expectObservable }) => {
      expectObservable(service.isAuthenticated$).toBe('a', { a: false });
      expectObservable(service.authToken$).toBe('a', { a: null });
    });
  });

  it('should store new token on refresh', () => {
    authAPIMock.signIn
      .withArgs('test', 'testpass')
      .and.returnValue(of({ accessToken: 'authToken', refreshToken: 'refresh', tokenType: 'Bearer' }));
    authAPIMock.refresh
      .withArgs('refresh')
      .and.returnValue(of({ accessToken: 'authToken2', refreshToken: 'refresh2', tokenType: 'Bearer' }));

    service.signIn('test', 'testpass').subscribe(() => {
      service.refreshToken().subscribe((accessToken) => {
        expect(accessToken).toEqual('authToken2');
        service.authToken$.pipe(take(1)).subscribe((token) => {
          expect(token).toEqual('authToken2');
        });
      });
    });
  });
});
