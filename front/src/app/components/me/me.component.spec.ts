// Importation des modules Angular nécessaires pour les tests et la fonctionnalité du composant
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { MeComponent } from './me.component';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { fakeAsync, tick } from '@angular/core/testing';

// Début des tests pour le composant MeComponent
describe('MeComponent', () => {

  // Tests unitaires pour le composant avec des services mockés
  describe('Unit Tests', () => {
    let component: MeComponent; // Instance du composant
    let fixture: ComponentFixture<MeComponent>; // Fixture pour gérer le DOM
    let sessionService: any; // Service de session mocké
    let userService: any; // Service utilisateur mocké
    let matSnackBar: any; // Mock du MatSnackBar
    let router: any; // Mock du Router

    // Configuration initiale des tests unitaires
    beforeEach(async () => {
      // Mocks des services nécessaires au composant
      const mockSessionService = {
        sessionInformation: { admin: true, id: '1' }, // Informations de session
        logOut: jest.fn() // Méthode mockée pour la déconnexion
      };

      const mockUserService = {
        getById: jest.fn().mockReturnValue(of({ id: 1, name: 'John Doe' })), // Mock de la méthode pour récupérer un utilisateur
        delete: jest.fn().mockReturnValue(of({})) // Mock de la méthode pour supprimer un utilisateur
      };

      const mockMatSnackBar = {
        open: jest.fn() // Mock de la méthode pour afficher une notification
      };

      const mockRouter = {
        navigate: jest.fn() // Mock de la méthode pour naviguer
      };

      // Configuration du module de test avec les dépendances nécessaires
      await TestBed.configureTestingModule({
        declarations: [MeComponent], // Déclaration du composant testé
        imports: [
          HttpClientTestingModule, // Module pour simuler les requêtes HTTP
          MatCardModule, // Module pour les cartes Material
          MatFormFieldModule, // Module pour les champs de formulaire Material
          MatIconModule, // Module pour les icônes Material
          MatInputModule // Module pour les champs d'entrée Material
        ],
        providers: [
          { provide: SessionService, useValue: mockSessionService }, // Fourniture du service mocké
          { provide: UserService, useValue: mockUserService },
          { provide: MatSnackBar, useValue: mockMatSnackBar },
          { provide: Router, useValue: mockRouter }
        ],
      }).compileComponents(); // Compilation des composants et des modules

      // Initialisation des variables pour chaque test
      fixture = TestBed.createComponent(MeComponent);
      component = fixture.componentInstance;
      sessionService = TestBed.inject(SessionService);
      userService = TestBed.inject(UserService);
      matSnackBar = TestBed.inject(MatSnackBar);
      router = TestBed.inject(Router);
      fixture.detectChanges(); // Détection des changements dans le DOM
    });

    // Test pour vérifier que le composant est créé
    it('should create', () => {
      expect(component).toBeTruthy(); // Vérification que l'instance du composant est définie
    });

    // Test pour s'assurer que ngOnInit récupère les données utilisateur
    it('should call ngOnInit and fetch user data', () => {
      component.ngOnInit(); // Appel de ngOnInit
      expect(userService.getById).toHaveBeenCalledWith('1'); // Vérification que getById a été appelé avec le bon paramètre
      expect(component.user).toEqual({ id: 1, name: 'John Doe' }); // Vérification des données utilisateur récupérées
    });

    // Test pour vérifier la méthode back()
    it('should call back()', () => {
      const spy = jest.spyOn(window.history, 'back'); // Espionnage de la méthode history.back
      component.back(); // Appel de la méthode back
      expect(spy).toHaveBeenCalled(); // Vérification que la méthode a été appelée
    });

    // Test pour vérifier la méthode delete()
    it('should call delete()', () => {
      component.delete(); // Appel de la méthode delete
      expect(userService.delete).toHaveBeenCalledWith('1'); // Vérification de l'appel à delete
      expect(matSnackBar.open).toHaveBeenCalledWith(
        "Your account has been deleted !", 'Close', { duration: 3000 }
      ); // Vérification de la notification affichée
      expect(sessionService.logOut).toHaveBeenCalled(); // Vérification que la déconnexion a été appelée
      expect(router.navigate).toHaveBeenCalledWith(['/']); // Vérification de la redirection après suppression
    });
  });

  // Tests d'intégration utilisant le vrai UserService
  describe('Integration Tests', () => {
    let component: MeComponent;
    let fixture: ComponentFixture<MeComponent>;
    let httpMock: HttpTestingController; // Mock pour les requêtes HTTP
    let sessionService: any;
    let matSnackBar: any;
    let router: any;

    // Configuration initiale pour les tests d'intégration
    beforeEach(async () => {
      const mockSessionService = {
        sessionInformation: { admin: true, id: '1' }, // Informations de session mockées
        logOut: jest.fn() // Mock de la déconnexion
      };

      const mockMatSnackBar = {
        open: jest.fn() // Mock de MatSnackBar
      };

      const mockRouter = {
        navigate: jest.fn() // Mock de Router
      };

      await TestBed.configureTestingModule({
        declarations: [MeComponent], // Déclaration du composant
        imports: [
          HttpClientTestingModule, // Module pour les requêtes HTTP simulées
          MatCardModule,
          MatFormFieldModule,
          MatIconModule,
          MatInputModule
        ],
        providers: [
          { provide: SessionService, useValue: mockSessionService }, // Fourniture du service mocké
          UserService, // Utilisation du vrai UserService
          { provide: MatSnackBar, useValue: mockMatSnackBar },
          { provide: Router, useValue: mockRouter }
        ],
      }).compileComponents();

      fixture = TestBed.createComponent(MeComponent);
      component = fixture.componentInstance;
      httpMock = TestBed.inject(HttpTestingController); // Injection du mock HTTP
      sessionService = TestBed.inject(SessionService);
      matSnackBar = TestBed.inject(MatSnackBar);
      router = TestBed.inject(Router);
    });

    // Nettoyage après chaque test
    afterEach(() => {
      httpMock.verify(); // Vérification qu'aucune requête HTTP inattendue n'a été faite
    });

    // Test pour vérifier la récupération des données utilisateur via HTTP
    it('should fetch user data via HTTP', (done) => {
      const mockUser = { id: 1, name: 'John Doe' }; // Données utilisateur simulées

      component.ngOnInit(); // Appel de ngOnInit

      const req = httpMock.expectOne('api/user/1'); // Vérification de la requête HTTP attendue
      expect(req.request.method).toBe('GET'); // Vérification que la méthode utilisée est GET
      req.flush(mockUser); // Simulation de la réponse de l'API

      setTimeout(() => {
        expect(component.user).toEqual(mockUser); // Vérification des données utilisateur récupérées
        done(); // Indication que le test est terminé
      });
    });

    // Test pour vérifier la suppression d'un utilisateur via HTTP
    it('should delete user via HTTP', (done) => {
      fixture.detectChanges(); // Détection des changements

      // Traitement de la requête initiale pour récupérer les données utilisateur
      const initialReq = httpMock.expectOne('api/user/1');
      initialReq.flush({ id: 1, name: 'John Doe' });

      // Appel de la méthode delete
      component.delete();

      // Vérification de la requête DELETE
      const deleteReq = httpMock.expectOne('api/user/1');
      expect(deleteReq.request.method).toBe('DELETE'); // Vérification que la méthode utilisée est DELETE
      deleteReq.flush({}); // Simulation de la réponse de suppression

      setTimeout(() => {
        expect(sessionService.logOut).toHaveBeenCalled(); // Vérification de la déconnexion
        expect(router.navigate).toHaveBeenCalledWith(['/']); // Vérification de la redirection
        done(); // Indication que le test est terminé
      });
    });
  });
});
