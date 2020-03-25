import { browser, by, element } from 'protractor';

export class LoginPage {
  navigateTo() {
    return browser.get('/login');
  }

  getLoginButton() {
    return element(by.css('button.btn.btn-primary'));
  }

  getUsernameField() {
    return element(by.id('clr-form-control-1'));
  }

  getPasswordField() {
    return element(by.id('clr-form-control-2'));
  }

  getUsernameErrorElement() {
    return element(by.id('clr-form-control-1-error'));
  }

  getPasswordErrorElement() {
    return element(by.id('clr-form-control-2-error'));
  }
}
