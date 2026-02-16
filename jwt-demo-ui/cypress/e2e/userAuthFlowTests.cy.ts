import { locators } from './../locators';
describe('User Auth Flow Tests', () => {
  let validUsername: string;
  let validPassword: string;
  let validAdminName: string;
  let validAdminPassword: string;
  let invalidUsername: string;
  let invalidPassword: string;
  let errorMessage: string;
  let userMessage: string;
  before(() => {
    cy.fixture('auth.json').then((authData) => {
      validUsername = authData.validUser.username;
      validPassword = authData.validUser.password;
      validAdminName = authData.validAdmin.username
      validAdminPassword = authData.validAdmin.password;
      invalidUsername = authData.invalidUser.username;
      invalidPassword = authData.invalidUser.password;
      errorMessage = authData.errorMessage;
      userMessage = authData.userMessage;
    });
  });

  beforeEach(() => {
    cy.visit('/login');
  });
  it('Should display error message for invalid credentials', () => {
    cy.login(invalidUsername, invalidPassword)
    // cy.get(locators.login.usernameInput).type(invalidUsername);
    // cy.get(locators.login.passwordInput).type(invalidPassword);
    // cy.get(locators.login.loginButton).click();
    cy.get(locators.login.errorMessage).should('contain', errorMessage);
  });

  it('Should navigate to dashbored for valid credentials', () => {
    cy.login(validUsername, validPassword)
    // cy.get(locators.login.usernameInput).type(validUsername);
    // cy.get(locators.login.passwordInput).type(validPassword);
    // cy.get(locators.login.loginButton).click();
    cy.url().should('include', '/user');
    cy.get(locators.dashboard.userMessage).should('contain', userMessage);
    cy.get(locators.dashboard.logoutButton).click();
    cy.url().should('include', '/login');
    cy.get(locators.dashboard.logoutButton).click();
  });

  
  it('Should navigate to dashbored for valid credentials with admin', () => {
    cy.login(validUsername, validPassword)
    // cy.get(locators.login.usernameInput).type(validUsername);
    // cy.get(locators.login.passwordInput).type(validPassword);
    // cy.get(locators.login.loginButton).click();
    cy.url().should('include', '/admin');
    // cy.get(locators.dashboard.adminMessage).should('contain', adminMessage);
    // cy.get(locators.dashboard.logoutButton).click();
    // cy.url().should('include', '/admin');
  });
});
