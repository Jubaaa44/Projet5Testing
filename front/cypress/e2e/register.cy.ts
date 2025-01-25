describe('Register', () => {
  it('should register a new user successfully', () => {
    cy.visit('/register');
    
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('newuser@example.com');
    cy.get('input[formControlName=password]').type('Password123');
    
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: { message: 'User registered successfully!' }
    }).as('registerUser');
    
    cy.get('form').submit();
    cy.wait('@registerUser');
    cy.url().should('include', '/login');
  });
 
  it('should show error on registration failure', () => {
    cy.visit('/register');
    
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('existing@example.com');
    cy.get('input[formControlName=password]').type('Password123');
    
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' }
    }).as('registerError');
    
    cy.get('form').submit();
    cy.wait('@registerError');
    cy.contains('An error occurred').should('be.visible');
  });
 
  it('should validate form fields', () => {
    cy.visit('/register');
    
    // Test email validation
    cy.get('input[formControlName=email]').type('invalid-email');
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formControlName=email]').clear().type('test@example.com');
    
    // Test firstName min length
    cy.get('input[formControlName=firstName]').type('Jo');
    cy.get('button[type="submit"]').should('be.disabled');
    
    // Test lastName min length
    cy.get('input[formControlName=lastName]').type('Do');
    cy.get('button[type="submit"]').should('be.disabled');
    
    // Complete valid form
    cy.get('input[formControlName=firstName]').clear().type('John');
    cy.get('input[formControlName=lastName]').clear().type('Doe');
    cy.get('input[formControlName=password]').type('Password123');
    
    // Form should be valid
    cy.get('button[type="submit"]').should('be.enabled');
  });
 });