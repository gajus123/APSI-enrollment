import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { timer } from 'rxjs';

@Component({
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isError: boolean;
  isLoading: boolean;

  constructor(public fb: FormBuilder) {
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

  tryAuthenticate(login: string, password: string) {
    this.isLoading = true;
    this.loginForm.disable();
    // TODO: use auth service from TG-14 instead of timer
    timer(1000).subscribe(() => {
      this.isLoading = false;
      this.loginForm.enable();
    });
  }
}
