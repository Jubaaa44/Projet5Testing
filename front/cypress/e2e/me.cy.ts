describe('Navigation to Me page', () => {
  it('Should navigate to Me page from home after login', () => {
    // Commencer par la page de login
    cy.visit('/login')

    // Intercepter la requête de login
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    // Intercepter la requête de session
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    // Saisir les informations de connexion
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    // Vérifier qu'on arrive sur la page des sessions
    cy.url().should('include', '/sessions')

    // Cliquer sur le lien Account dans la navbar
    cy.get('span.link').contains('Account').click()

    // Vérifier qu'on arrive bien sur la page "me"
    cy.url().should('include', '/me')
    
    // Vérifier que le titre de la page est bien présent
    cy.contains('User information').should('be.visible')
  })
})

describe('Delete account', () => {
  it('Should delete account when clicking on delete button', () => {
    // Intercepter toutes les requêtes nécessaires avant de commencer
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: false
      },
    })

    cy.intercept('GET', '/api/session', []).as('session')

    cy.intercept('GET', '/api/user/*', {
      body: {
        id: 1,
        firstName: 'firstName',
        lastName: 'lastName',
        email: 'yoga@studio.com',
        admin: false,
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      }
    }).as('getUser')

    cy.intercept('DELETE', '/api/user/*', {
      statusCode: 200,
      body: {}
    }).as('deleteUser')

    // Commencer la navigation
    cy.visit('/login')

    // Saisir les informations de connexion
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    // Attendre que la redirection soit effectuée
    cy.url().should('include', '/sessions')

    // Naviguer vers la page "me"
    cy.get('span.link').contains('Account').click()

    // Vérifier qu'on est bien sur la page "me"
    cy.url().should('include', '/me')

    // Vérifier que le bouton de suppression est visible
    cy.contains('Delete my account').should('be.visible')

    // Cliquer sur le bouton de suppression
    cy.contains('button', 'Detail').click()

    // Attendre la requête de suppression
    cy.wait('@deleteUser')

    // Vérifier la redirection vers la page d'accueil
    cy.url().should('eq', Cypress.config().baseUrl + '/')

    // Vérifier que la notification de suppression est affichée
    cy.contains('Your account has been deleted !').should('be.visible')
  })
})

describe('Admin view', () => {
  it('Should not show delete button for admin user', () => {
    // Intercepter toutes les requêtes nécessaires avant de commencer
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'adminUser',
        firstName: 'Admin',
        lastName: 'User',
        admin: true  // Important : définir l'utilisateur comme admin
      },
    })

    cy.intercept('GET', '/api/session', []).as('session')

    cy.intercept('GET', '/api/user/*', {
      body: {
        id: 1,
        firstName: 'Admin',
        lastName: 'User',
        email: 'admin@studio.com',
        admin: true,
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      }
    }).as('getUser')

    // Commencer la navigation
    cy.visit('/login')

    // Saisir les informations de connexion
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    // Attendre que la redirection soit effectuée
    cy.url().should('include', '/sessions')

    // Naviguer vers la page "me"
    cy.get('span.link').contains('Account').click()

    // Vérifier qu'on est bien sur la page "me"
    cy.url().should('include', '/me')

    // Vérifier que le message admin est affiché
    cy.contains('You are admin').should('be.visible')

    // Vérifier que le bouton de suppression n'est pas présent
    cy.contains('Delete my account').should('not.exist')
    cy.contains('button', 'Detail').should('not.exist')
  })
})

describe('User Information Display', () => {
  it('Should display all user information correctly', () => {
    // Préparer les données de test
    const testUser = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@studio.com',
      admin: false,
      createdAt: '2024-01-15T10:00:00Z',
      updatedAt: '2024-01-17T15:30:00Z'
    };

    // Intercepter toutes les requêtes nécessaires
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: testUser.id,
        username: 'johnDoe',
        firstName: testUser.firstName,
        lastName: testUser.lastName,
        admin: testUser.admin
      },
    })

    cy.intercept('GET', '/api/session', []).as('session')

    cy.intercept('GET', '/api/user/*', {
      body: testUser
    }).as('getUser')

    // Commencer la navigation
    cy.visit('/login')

    // Se connecter
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    // Attendre la redirection
    cy.url().should('include', '/sessions')

    // Aller sur la page "me"
    cy.get('span.link').contains('Account').click()

    // Vérifier l'URL
    cy.url().should('include', '/me')

    // Vérifier le titre
    cy.contains('h1', 'User information').should('be.visible')

    // Vérifier toutes les informations utilisateur
    cy.contains('p', `Name: ${testUser.firstName} ${testUser.lastName.toUpperCase()}`).should('be.visible')
    cy.contains('p', `Email: ${testUser.email}`).should('be.visible')

    // Vérifier les dates (on utilise des dates localisées, donc on vérifie juste leur présence)
    cy.contains('Create at:').should('be.visible')
    cy.contains('Last update:').should('be.visible')

    // Comme c'est un utilisateur non-admin, vérifier la présence du bouton de suppression
    cy.contains('Delete my account').should('be.visible')
    cy.contains('button', 'Detail').should('be.visible')

    // Vérifier que le message admin n'est pas présent
    cy.contains('You are admin').should('not.exist')
  })

  describe('Error Handling', () => {
    beforeEach(() => {
      const mockUser = {
        token: 'fake-token',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      };
   
      cy.window().then((window) => {
        window.sessionStorage.setItem('token', mockUser.token);
        window.localStorage.setItem('session', JSON.stringify(mockUser));
      });
   
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
        body: mockUser
      }).as('login');
   
      cy.intercept('GET', '/api/session', []).as('sessions');
    });
   
    it('should handle failed user data loading', () => {
      cy.intercept('GET', '/api/user/*', {
        forceNetworkError: true
      }).as('getUserError');
  
      cy.visit('/login');
      cy.get('input[formControlName=email]').type("yoga@studio.com");
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
  
      cy.get('span.link').contains('Account').click();
      cy.wait('@getUserError');
      cy.url().should('include', '/me');
    });
  
    it('should handle failed delete request', () => {
      cy.intercept('GET', '/api/user/*', {
        body: {
          id: 1,
          firstName: 'Test',
          lastName: 'User',
          email: 'test@test.com',
          admin: false,
          createdAt: '2024-01-15T10:00:00Z',
          updatedAt: '2024-01-15T10:00:00Z'
        }
      });
  
      cy.intercept('DELETE', '/api/user/*', {
        forceNetworkError: true
      }).as('deleteError');
  
      cy.visit('/login');
      cy.get('input[formControlName=email]').type("yoga@studio.com");
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
  
      cy.get('span.link').contains('Account').click();
      cy.contains('button', 'Detail').click();
      cy.wait('@deleteError');
      cy.url().should('include', '/me');
    });
   });
  
  describe('URL Navigation', () => {
    it('should redirect to login when accessing /me without authentication', () => {
      cy.visit('/me');
      cy.url().should('include', '/login');
    });
  });
  
})