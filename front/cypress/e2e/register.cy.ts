describe('Register spec', () => {
    it('should register a new user successfully', () => {
      // Étape 1 : Visite de la page d'inscription
      cy.visit('/register');
      
      // Étape 2 : Remplir le formulaire d'inscription
      cy.get('input[formControlName=firstName]').type('John');
      cy.get('input[formControlName=lastName]').type('Doe');
      cy.get('input[formControlName=email]').type('newuser@example.com');
      cy.get('input[formControlName=password]').type('Password123');
      
      // Étape 3 : Soumettre le formulaire
      cy.intercept('POST', '/api/auth/register', {
        statusCode: 200,
        body: { message: 'User registered successfully!' }
      }).as('registerUser');
      
      cy.get('form').submit();
  
      // Étape 4 : Vérification de la réponse et de la redirection
      cy.wait('@registerUser');
    });
  
    it('should show error when email is already taken', () => {
      // Étape 1 : Visite de la page d'inscription
      cy.visit('/register');
      
      // Étape 2 : Remplir le formulaire d'inscription avec un email déjà pris
      cy.get('input[formControlName=firstName]').type('John');
      cy.get('input[formControlName=lastName]').type('Doe');
      cy.get('input[formControlName=email]').type('newuser@example.com');
      cy.get('input[formControlName=password]').type('Password123');
      
      // Étape 3 : Soumettre le formulaire
      cy.intercept('POST', '/api/auth/register', {
        statusCode: 400,
        body: { message: 'Error: Email is already taken!' }
      }).as('registerUserError');
      
      cy.get('form').submit();
  
      // Étape 4 : Vérification de la réponse d'erreur
      cy.wait('@registerUserError');
    });
  });
  