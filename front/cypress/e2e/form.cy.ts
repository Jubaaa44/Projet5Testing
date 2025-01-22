describe('Session Form', () => {
  beforeEach(() => {
    // Configuration du token et user admin
    const mockUser = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'admin',
      firstName: 'Admin',
      lastName: 'User',
      admin: true
    };

    // Setup du storage
    cy.window().then((window) => {
      window.sessionStorage.setItem('token', mockUser.token);
      window.localStorage.setItem('session', JSON.stringify(mockUser));
    });

    // Interceptions
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: mockUser
    }).as('login');

    cy.intercept('GET', '**/api/session', {
      statusCode: 200,
      body: []
    }).as('getSessions');

    cy.intercept('GET', '**/api/teacher', {
      statusCode: 200,
      body: [
        { id: 1, firstName: 'John', lastName: 'Doe' },
        { id: 2, firstName: 'Jane', lastName: 'Smith' }
      ]
    }).as('getTeachers');

    // Intercepter la création
    cy.intercept('POST', '**/api/session', {
      statusCode: 201,
      body: {
        id: 1,
        name: 'Test Session',
        date: '2025-01-22',
        teacher_id: 1,
        description: 'Test Description'
      }
    }).as('createSession');

    // Login
    cy.visit('/login');
    cy.get('input[formControlName="email"]').type('yoga@studio.com');
    cy.get('input[formControlName="password"]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');

    // Attente de la redirection et navigation
    cy.url().should('include', '/sessions');
    cy.get('button[routerLink="create"]').should('exist').click();
  });

  it('should handle form validation', () => {
    // Vérification initiale du bouton submit
    cy.get('button[type="submit"]').should('be.disabled');

    // Test de la validation des champs
    cy.get('form').within(() => {
      // Test du champ name
      cy.get('input[formControlName="name"]')
        .should('be.visible')
        .focus()
        .blur();
        
      // Vérification que le champ est invalide
      cy.get('input[formControlName="name"]')
        .should('have.class', 'ng-invalid');

      // Remplissage progressif
      cy.get('input[formControlName="name"]').type('Test Session');
      cy.get('input[formControlName="date"]').type('2025-01-22');
    });

    // Sélection de l'enseignant (en dehors du within car mat-option est dans l'overlay)
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').first().click();

    // Retour au formulaire
    cy.get('form').within(() => {
      cy.get('textarea[formControlName="description"]').type('Test Description');
      // Vérification que le bouton est activé une fois tous les champs remplis
      cy.get('button[type="submit"]').should('be.enabled');
    });
  });

  it('should create new session successfully', () => {
    // Remplissage du formulaire
    cy.get('form').within(() => {
      cy.get('input[formControlName="name"]').type('Test Session');
      cy.get('input[formControlName="date"]').type('2025-01-22');
    });

    // Sélection de l'enseignant
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').first().click();

    // Fin du remplissage et soumission
    cy.get('form').within(() => {
      cy.get('textarea[formControlName="description"]').type('Test Description');
      cy.get('button[type="submit"]')
        .should('be.enabled')
        .click();
    });

    // Vérification
    cy.wait('@createSession');
    cy.url().should('include', '/sessions');
  });

  it('should handle back navigation', () => {
    cy.get('button[mat-icon-button][routerLink="/sessions"]').click();
    cy.url().should('include', '/sessions');
  });
});