describe('Login', () => {
  it('Login successful', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    });

    cy.intercept('GET', '/api/session', []).as('session');

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.url().should('include', '/sessions');
  });

  it('Should show error message on login failure', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' }
    }).as('loginError');

    cy.get('input[formControlName=email]').type("wrong@email.com");
    cy.get('input[formControlName=password]').type(`${"wrongpass"}{enter}{enter}`);
    
    cy.wait('@loginError');
    cy.contains('An error occurred').should('be.visible');
  });

  it('Should validate form fields', () => {
    cy.visit('/login');

    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formControlName=email]').type('invalid-email');
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formControlName=email]').clear().type('valid@email.com');
    cy.get('input[formControlName=password]').type('pwd');
    cy.get('button[type="submit"]').should('be.enabled');
  });

  it('Should toggle password visibility', () => {
    cy.visit('/login');

    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password');
    cy.get('button[matSuffix]').click();
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'text');
  });
});