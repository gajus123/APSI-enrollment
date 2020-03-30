import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/auth/auth.service';

@Component({
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isError: boolean;
  isLoading: boolean;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });

    this.isError = false;
    this.isLoading = false;
  }

  ngOnInit() {}

  onSubmit() {
    if (this.isLoading) {
      return;
    }

    const { username, password } = this.loginForm.value;
    this.loginForm.markAllAsTouched();
    if (this.loginForm.valid) {
      this.tryAuthenticate(username, password);
    }
  }

  tryAuthenticate(username: string, password: string) {
    this.isLoading = true;
    this.loginForm.disable();
    this.authService.signIn(username, password).subscribe(
      () => {
        this.router.navigate(['/main']);
      },
      () => {
        this.isError = true;
        this.isLoading = false;
        this.loginForm.enable();
      }
    );
  }
}
