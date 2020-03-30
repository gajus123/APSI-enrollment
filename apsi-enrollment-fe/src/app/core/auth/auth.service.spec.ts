import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { take } from 'rxjs/operators';
import { TestScheduler } from 'rxjs/testing';
import { environment } from 'src/environments/environment';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let scheduler: TestScheduler;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
    scheduler = new TestScheduler((actual, expected) => {
      expect(actual).toEqual(expected);
    });

    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should authenticate when successfully retrieved token', () => {
    service.signIn('test', 'testpass').subscribe();
    const req = httpMock.expectOne(`${environment.authBaseUrl}/authenticate`);
    expect(req.request.body).toEqual({ username: 'test', password: 'testpass' });
    req.flush({ authToken: 'authToken', refreshToken: 'refresh' });

    scheduler.run(({ expectObservable }) => {
      expectObservable(service.isAuthenticated$).toBe('a', { a: true });
    });
  }, 2000);

  it('should not authenticate when cannot retrieve token', () => {
    service.signIn('test', 'testpass').subscribe(
      () => {},
      (err) => {
        expect(err.status).toEqual(400);
      },
      () => {}
    );

    const req = httpMock.expectOne(`${environment.authBaseUrl}/authenticate`);
    expect(req.request.body).toEqual({ username: 'test', password: 'testpass' });
    req.flush({}, { status: 400, statusText: 'Bad Request' });

    scheduler.run(({ expectObservable }) => {
      expectObservable(service.isAuthenticated$).toBe('a', { a: false });
    });
  }, 2000);

  it('should remove token on sign out', () => {
    service.signOut().subscribe();

    scheduler.run(({ expectObservable }) => {
      expectObservable(service.isAuthenticated$).toBe('a', { a: false });
      expectObservable(service.authToken$).toBe('a', { a: null });
    });
  });

  it('should store new token on refresh', () => {
    service.signIn('test', 'testpass').subscribe();
    const req = httpMock.expectOne(`${environment.authBaseUrl}/authenticate`);
    expect(req.request.body).toEqual({ username: 'test', password: 'testpass' });
    req.flush({ authToken: 'authToken', refreshToken: 'refresh' });

    service.authToken$.pipe(take(1)).subscribe((token) => {
      expect(token).toEqual('authToken');
    });

    service.refreshToken().subscribe();
    const req2 = httpMock.expectOne(`${environment.authBaseUrl}/refresh`);
    req2.flush({ authToken: 'authToken2', refreshToken: 'refresh2' });

    service.authToken$.pipe(take(1)).subscribe((token) => {
      expect(token).toEqual('authToken2');
    });
  });
});
