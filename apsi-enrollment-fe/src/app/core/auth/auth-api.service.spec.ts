import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from 'src/environments/environment';
import { AuthAPIService } from './auth-api.service';

describe('AuthAPIService', () => {
  let service: AuthAPIService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(AuthAPIService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should authenticate when successfully retrieved token', () => {
    service.signIn('test', 'testpass').subscribe((token) => {
      expect(token).toEqual({ accessToken: 'authToken', refreshToken: 'refresh', tokenType: 'Bearer' });
    });
    const req = httpMock.expectOne(`${environment.authBaseUrl}/authenticate`);
    expect(req.request.body).toEqual({ username: 'test', password: 'testpass' });
    req.flush({ accessToken: 'authToken', refreshToken: 'refresh', tokenType: 'Bearer' });
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
  }, 2000);
});
