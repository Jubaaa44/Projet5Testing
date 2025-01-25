describe('Session Detail', () => {
  it('should navigate to detail page', () => {
    const authToken = 'fake-jwt-token';
    const mockUser = {
      token: authToken,
      type: 'Bearer',
      id: 5,
      username: 'user',
      firstName: 'Regular',
      lastName: 'User',
      admin: false
    };

    // Set up initial session storage
    cy.window().then((window) => {
      window.sessionStorage.setItem('token', authToken);
      window.localStorage.setItem('session', JSON.stringify(mockUser));
    });

    // Intercepter le login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: mockUser
    }).as('login');

    // Intercepter les requêtes API
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: []
    }).as('getSessions');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');

    // Vérifier qu'on est bien sur la page des sessions
    cy.url().should('include', '/sessions');

    // Intercepter la requête de détail avant la navigation
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4],
        createdAt: new Date('2024-01-15T10:00:00Z'),
        updatedAt: new Date('2024-01-15T10:00:00Z')
      }
    }).as('getSession');

    // Tenter d'accéder à la page de détail avec la bonne route
    cy.visit('/sessions/detail/1');
  });

  it('should navigate to detail page from sessions list', () => {
    const authToken = 'fake-jwt-token';
    const mockUser = {
      token: authToken,
      type: 'Bearer',
      id: 5,
      username: 'user',
      firstName: 'Regular',
      lastName: 'User',
      admin: false
    };

    // Set up initial session storage
    cy.window().then((window) => {
      window.sessionStorage.setItem('token', authToken);
      window.localStorage.setItem('session', JSON.stringify(mockUser));
    });

    // Intercepter le login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: mockUser
    }).as('login');

    // Intercepter la liste des sessions avec une session
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [{
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4]
      }]
    }).as('getSessions');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');

    // Attendre que la liste des sessions soit chargée
    cy.wait('@getSessions');

    // Intercepter la requête de détail
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4],
        createdAt: new Date('2024-01-15T10:00:00Z'),
        updatedAt: new Date('2024-01-15T10:00:00Z')
      }
    }).as('getSession');

    // Cliquer sur le bouton Detail
    cy.contains('button', 'Detail').click();

    // Vérifier qu'on arrive sur la page de détail
    cy.url().should('include', '/sessions/detail/1');
  });

  it('should handle participation and unparticipation correctly', () => {
    const authToken = 'fake-jwt-token';
    const mockUser = {
      token: authToken,
      type: 'Bearer',
      id: 5,
      username: 'user',
      firstName: 'Regular',
      lastName: 'User',
      admin: false
    };

    // Set up initial session storage
    cy.window().then((window) => {
      window.sessionStorage.setItem('token', authToken);
      window.localStorage.setItem('session', JSON.stringify(mockUser));
    });

    // Intercepter le login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: mockUser
    }).as('login');

    // Intercepter la liste des sessions avec une session
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [{
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4]
      }]
    }).as('getSessions');

    // Intercepter la requête de détail initiale
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4],
        createdAt: new Date('2024-01-15T10:00:00Z'),
        updatedAt: new Date('2024-01-15T10:00:00Z')
      }
    }).as('getSession');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');

    // Attendre que la liste des sessions soit chargée
    cy.wait('@getSessions');

    // Cliquer sur le bouton Detail
    cy.contains('button', 'Detail').click();

    // Attendre que les détails soient chargés
    cy.wait('@getSession');

    // Intercepter la requête de participation
    cy.intercept('POST', '/api/session/1/participate/5', {
      statusCode: 200
    }).as('participate');

    // Intercepter la requête de détail après participation
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4, 5],  // L'utilisateur 5 est maintenant inscrit
        createdAt: new Date('2024-01-15T10:00:00Z'),
        updatedAt: new Date('2024-01-15T10:00:00Z')
      }
    }).as('getSessionAfterParticipate');

    // Participer à la session
    cy.contains('button', 'Participate').click();
    cy.wait('@participate');
    cy.wait('@getSessionAfterParticipate');
    
    // Vérifier le changement de bouton
    cy.contains('button', 'Do not participate').should('be.visible');

    // Intercepter la requête de désinscription
    cy.intercept('DELETE', '/api/session/1/participate/5', {
      statusCode: 200
    }).as('unparticipate');

    // Intercepter la requête de détail après désinscription
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'yoga for beginners',
        description: 'A gentle introduction to yoga',
        date: new Date('2024-02-01T10:00:00Z'),
        teacher_id: 1,
        users: [2, 3, 4],  // L'utilisateur 5 est retiré
        createdAt: new Date('2024-01-15T10:00:00Z'),
        updatedAt: new Date('2024-01-15T10:00:00Z')
      }
    }).as('getSessionAfterUnparticipate');

    // Se désinscrire
    cy.contains('button', 'Do not participate').click();
    cy.wait('@unparticipate');
    cy.wait('@getSessionAfterUnparticipate');

    // Vérifier le retour au bouton initial
    cy.contains('button', 'Participate').should('be.visible');
  });

  it('should handle API errors on session load', () => {
    const authToken = 'fake-jwt-token';
    const mockUser = {
      token: authToken,
      type: 'Bearer',
      id: 5,
      username: 'user',
      firstName: 'Regular',
      lastName: 'User',
      admin: false
    };
  
    // Set up initial session storage
    cy.window().then((window) => {
      window.sessionStorage.setItem('token', authToken);
      window.localStorage.setItem('session', JSON.stringify(mockUser));
    });
  
    // Intercepter le login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: mockUser
    }).as('login');
  
    // Intercepter les sessions
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [{
        id: 1,
        name: 'yoga',
        description: 'test',
        date: new Date(),
        teacher_id: 1,
        users: []
      }]
    }).as('getSessions');
  
    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');
    cy.wait('@getSessions');
  
    // Intercepter l'erreur de détail session après la navigation
    cy.intercept('GET', '/api/session/1', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('sessionError');
  
    // Cliquer sur le bouton Detail
    cy.contains('button', 'Detail').click();
    cy.wait('@sessionError');
    cy.get('mat-card').should('not.exist');
  });

  it('should handle session deletion for admin user', () => {
    const authToken = 'fake-jwt-token';
    const mockUser = {
      token: authToken,
      type: 'Bearer',
      id: 5,
      username: 'user',
      firstName: 'Admin',
      lastName: 'User',
      admin: true
    };
  
    // Set up initial session storage
    cy.window().then((window) => {
      window.sessionStorage.setItem('token', authToken);
      window.localStorage.setItem('session', JSON.stringify(mockUser));
    });
  
    // Intercepter le login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: mockUser
    }).as('login');
  
    // Intercepter les sessions
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [{
        id: 1,
        name: 'yoga',
        description: 'test',
        date: new Date(),
        teacher_id: 1,
        users: []
      }]
    }).as('getSessions');
  
    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);
    cy.wait('@login');
    cy.wait('@getSessions');
  
    // Intercepter la requête de détail
    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: {
        id: 1,
        name: 'yoga',
        description: 'test',
        date: new Date(),
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    }).as('getSession');
  
    // Intercepter la suppression
    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200
    }).as('deleteSession');
  
    // Cliquer sur Detail puis Delete
    cy.contains('button', 'Detail').click();
    cy.wait('@getSession');
    cy.contains('button', 'Delete').click();
    cy.wait('@deleteSession');
  
    // Vérifier la redirection et le message
    cy.url().should('include', '/sessions');
    cy.contains('Session deleted !').should('be.visible');
  });
});