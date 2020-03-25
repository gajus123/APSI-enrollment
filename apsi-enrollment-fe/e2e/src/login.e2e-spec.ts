import { browser, Browser, by, ExpectedConditions, logging, promise } from 'protractor';
import { LoginPage } from './login.po';

describe('Login page', () => {
  let page: LoginPage;

  beforeEach(() => {
    page = new LoginPage();
    browser.waitForAngularEnabled(true);
  });

  it('should display error messages when input fields are empty', () => {
    page.navigateTo();
    page.getLoginButton().click();
    expect(page.getUsernameErrorElement()).toBeTruthy();
    expect(page.getPasswordErrorElement()).toBeTruthy();
  });

  it('should not display error messages when input fields are not empty', () => {
    page.navigateTo();
    page.getUsernameField().sendKeys('test');
    page.getPasswordField().sendKeys('test');
    page.getLoginButton().click();
    expect(page.getUsernameErrorElement().isPresent()).toBe(false);
    expect(page.getPasswordErrorElement().isPresent()).toBe(false);
  });

  it('should disable inputs after clicking login button (with valid data)', () => {
    browser.waitForAngularEnabled(false);

    page.navigateTo();
    const userField = page.getUsernameField();
    const passwordField = page.getPasswordField();
    const button = page.getLoginButton();
    userField.sendKeys('test');
    passwordField.sendKeys('test');
    button.click();

    expect(userField.isEnabled()).toBe(false);
    expect(passwordField.isEnabled()).toBe(false);
  });

  it('should change button text to spinner after clicking login button (with valid data)', () => {
    browser.waitForAngularEnabled(false);
    page.navigateTo();
    page.getUsernameField().sendKeys('test');
    page.getPasswordField().sendKeys('test');
    const button = page.getLoginButton();
    button.click();

    expect(button.element(by.className('spinner')).isPresent()).toBe(true);
    expect(button.getText()).toEqual('');
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser
      .manage()
      .logs()
      .get(logging.Type.BROWSER);
    expect(logs).not.toContain(
      jasmine.objectContaining({
        level: logging.Level.SEVERE,
      } as logging.Entry)
    );
  });
});
