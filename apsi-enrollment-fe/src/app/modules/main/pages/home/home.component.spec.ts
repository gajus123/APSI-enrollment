import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthService } from 'src/app/core/auth/auth.service';
import { CurrentUserService } from 'src/app/core/auth/current-user.service';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  const authService = jasmine.createSpyObj('AuthService', ['signOut']);
  const currentUserService = jasmine.createSpyObj('AuthAPIService', [], { currentUser$: of(null) });

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HomeComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: CurrentUserService, useValue: currentUserService },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
