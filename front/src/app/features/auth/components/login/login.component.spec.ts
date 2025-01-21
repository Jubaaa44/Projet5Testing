import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { LoginComponent } from './login.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect } from '@jest/globals';

// Définition des tests pour le composant LoginComponent
describe('LoginComponent', () => {
  // Tests unitaires avec services mockés
  describe('Unit Tests', () => {
    let component: LoginComponent; // Instance du composant
    let fixture: ComponentFixture<LoginComponent>; // Fixture pour tester le DOM et l'état du composant
    let mockAuthService: any; // Mock du service d'authentification
    let mockSessionService: any; // Mock du service de session
    let mockRouter: any; // Mock du service de navigation

    // Configuration avant chaque test
    beforeEach(async () => {
      // Création des mocks des services
      mockAuthService = {
        login: jest.fn(), // Mock de la méthode login
      };

      mockSessionService = {
        logIn: jest.fn(), // Mock de la méthode logIn
      };

      mockRouter = {
        navigate: jest.fn(), // Mock de la méthode navigate
      };

      // Configuration du module de test
      await TestBed.configureTestingModule({
        declarations: [LoginComponent], // Déclare le composant testé
        providers: [
          { provide: AuthService, useValue: mockAuthService }, // Fournit le mock du service AuthService
          { provide: SessionService, useValue: mockSessionService }, // Fournit le mock du service SessionService
          { provide: Router, useValue: mockRouter }, // Fournit le mock du Router
        ],
        imports: [
          RouterTestingModule, // Module de test pour la navigation
          BrowserAnimationsModule, // Nécessaire pour Angular Material
          HttpClientModule, // Module pour les requêtes HTTP
          MatCardModule, // Module pour le style de la carte
          MatIconModule, // Module pour les icônes
          MatFormFieldModule, // Module pour les champs de formulaire
          MatInputModule, // Module pour les champs de saisie
          ReactiveFormsModule, // Module pour les formulaires réactifs
        ],
      }).compileComponents(); // Compile le module et ses composants

      // Crée l'instance du composant et initialise les tests
      fixture = TestBed.createComponent(LoginComponent);
      component = fixture.componentInstance;
      fixture.detectChanges(); // Déclenche la détection des changements
    });

    // Vérifie que le composant est créé
    it('should create the component', () => {
      expect(component).toBeTruthy(); // Le composant doit exister
    });

    // Teste la soumission du formulaire et la connexion réussie
    it('should submit the form and log in the user on success', () => {
      const loginRequest = { email: 'test@example.com', password: 'password123' }; // Données de connexion
      const mockResponse = { userId: 1, email: 'test@example.com' }; // Réponse simulée

      mockAuthService.login.mockReturnValue(of(mockResponse)); // Simule une réponse réussie
      component.form.setValue(loginRequest); // Remplit le formulaire avec les données
      component.submit(); // Soumet le formulaire

      // Vérifie que les méthodes des services sont appelées avec les bons arguments
      expect(mockAuthService.login).toHaveBeenCalledWith(loginRequest);
      expect(mockSessionService.logIn).toHaveBeenCalledWith(mockResponse);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']); // Vérifie la navigation après connexion
    });

    // Teste la gestion des erreurs lors de la connexion
    it('should set onError to true when login fails', () => {
      const loginRequest = { email: 'test@example.com', password: 'password123' }; // Données de connexion
      mockAuthService.login.mockReturnValue(throwError(() => new Error('Login failed'))); // Simule une erreur

      component.form.setValue(loginRequest); // Remplit le formulaire avec les données
      component.submit(); // Soumet le formulaire

      // Vérifie que l'état d'erreur est défini
      expect(component.onError).toBe(true);
      expect(mockSessionService.logIn).not.toHaveBeenCalled(); // logIn ne doit pas être appelé
      expect(mockRouter.navigate).not.toHaveBeenCalled(); // Pas de navigation
    });

    // Vérifie la validation des champs du formulaire
    it('should validate the form fields', () => {
      component.form.controls['email'].setValue(''); // Champ email vide
      component.form.controls['password'].setValue(''); // Champ password vide
      expect(component.form.valid).toBeFalsy(); // Le formulaire ne doit pas être valide

      component.form.controls['email'].setValue('test@example.com'); // Email valide
      component.form.controls['password'].setValue('password123'); // Mot de passe valide
      expect(component.form.valid).toBeTruthy(); // Le formulaire doit être valide
    });
  });

  // Tests d'intégration avec vrai AuthService
  describe('Integration Tests', () => {
    let component: LoginComponent; // Instance du composant
    let fixture: ComponentFixture<LoginComponent>; // Fixture pour tester le DOM et l'état
    let httpMock: HttpTestingController; // Contrôle des requêtes HTTP simulées
    let sessionService: any; // Service de session réel ou mocké
    let router: any; // Service de navigation réel ou mocké

    // Configuration avant chaque test
    beforeEach(async () => {
      const mockSessionService = {
        logIn: jest.fn(), // Mock de la méthode logIn
      };

      const mockRouter = {
        navigate: jest.fn(), // Mock de la méthode navigate
      };

      // Configuration du module de test
      await TestBed.configureTestingModule({
        declarations: [LoginComponent], // Déclare le composant testé
        imports: [
          HttpClientTestingModule, // Module pour les tests des requêtes HTTP
          RouterTestingModule, // Module de test pour la navigation
          BrowserAnimationsModule, // Nécessaire pour Angular Material
          MatCardModule, // Module pour le style de la carte
          MatIconModule, // Module pour les icônes
          MatFormFieldModule, // Module pour les champs de formulaire
          MatInputModule, // Module pour les champs de saisie
          ReactiveFormsModule, // Module pour les formulaires réactifs
        ],
        providers: [
          AuthService, // Fournit le vrai AuthService
          { provide: SessionService, useValue: mockSessionService }, // Mock de SessionService
          { provide: Router, useValue: mockRouter }, // Mock du Router
        ],
      }).compileComponents(); // Compile le module et ses composants

      fixture = TestBed.createComponent(LoginComponent); // Crée l'instance du composant
      component = fixture.componentInstance; // Instance du composant
      httpMock = TestBed.inject(HttpTestingController); // Mock des requêtes HTTP
      sessionService = TestBed.inject(SessionService); // Mock ou instance réelle de SessionService
      router = TestBed.inject(Router); // Mock ou instance réelle du Router
      fixture.detectChanges(); // Déclenche la détection des changements
    });

    // Vérifie les requêtes restantes après chaque test
    afterEach(() => {
      httpMock.verify(); // S'assure qu'il n'y a pas de requêtes non gérées
    });

    // Teste la gestion de plusieurs tentatives de connexion échouées puis une connexion réussie
    it('should handle multiple failed login attempts then successful login', fakeAsync(() => {
      const loginRequest = { email: 'test@example.com', password: 'wrongpassword' }; // Données incorrectes

      fixture.ngZone?.run(() => {
        component.form.setValue(loginRequest); // Remplit le formulaire
        component.submit(); // Soumet le formulaire
      });

      const loginReq = httpMock.expectOne('api/auth/login'); // Attend une requête HTTP
      loginReq.error(new ErrorEvent('Network error')); // Simule une erreur réseau

      tick(); // Termine les tâches asynchrones

      expect(component.onError).toBeTruthy(); // Vérifie que l'erreur est enregistrée

      component.onError = false; // Réinitialise l'état d'erreur

      const mockResponse = { userId: 1, email: 'test@example.com' }; // Réponse correcte

      fixture.ngZone?.run(() => {
        component.submit(); // Réessaie la soumission du formulaire
      });

      const secondReq = httpMock.expectOne('api/auth/login'); // Nouvelle requête HTTP
      secondReq.flush(mockResponse); // Répond avec une réponse correcte

      tick(); // Termine les tâches asynchrones

      // Vérifie que tout fonctionne correctement après la réussite
      expect(component.onError).toBeFalsy();
      expect(sessionService.logIn).toHaveBeenCalledWith(mockResponse); // Vérifie l'appel de logIn
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']); // Vérifie la navigation
    }));
  });
});
