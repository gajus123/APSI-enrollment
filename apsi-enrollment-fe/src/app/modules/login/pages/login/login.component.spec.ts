import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { AuthService } from 'src/app/core/auth/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['signIn', 'signOut']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      declarations: [LoginComponent],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('login with valid credentials should navigate to main page', () => {
    component.loginForm.setValue({ username: 'test', password: 'testpass' });
    authServiceSpy.signIn.withArgs('test', 'testpass').and.returnValue(of(true));

    component.onSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/main']);
    expect(component.isError).toBe(false);
  });

  it('login with invalid credentials should report error', () => {
    component.loginForm.setValue({ username: 'test', password: 'testpass' });
    authServiceSpy.signIn.withArgs('test', 'testpass').and.returnValue(throwError(null));
    component.onSubmit();
    expect(component.isError).toBe(true);
  });
});
