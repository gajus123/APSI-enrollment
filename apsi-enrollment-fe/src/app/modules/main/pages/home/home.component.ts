import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/core/auth/auth.service';
import { CurrentUserService } from 'src/app/core/auth/current-user.service';
import { User } from 'src/app/core/model/user.model';

@Component({
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  currentUser$: Observable<User>;

  constructor(private currentUserService: CurrentUserService, private authService: AuthService) {
    this.currentUser$ = currentUserService.currentUser$;
  }

  ngOnInit(): void {}

  onLogout() {
    this.authService.signOut().subscribe();
  }
}
