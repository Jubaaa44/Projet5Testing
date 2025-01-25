// Importation des modules nécessaires pour le test
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { FormComponent } from './form.component';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { Session } from '../../interfaces/session.interface';

// Mock des méthodes d'animation et d'écouteurs d'événements pour les tests
Object.defineProperty(window.Element.prototype, 'animate', { value: jest.fn() });
Object.defineProperty(window, 'addEventListener', { value: jest.fn() });

// Début des tests unitaires pour le composant FormComponent
describe('FormComponent - Unit Tests', () => {
  let component: FormComponent;
  let router: Router;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let mockMatSnackBar: jest.Mocked<MatSnackBar>;

  // Simulation des services utilisés par le composant
  const mockSessionService = {
    sessionInformation: { admin: true }
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([])) // Retourne un observable vide pour les enseignants
  };

  const mockSessionApiService = {
    create: jest.fn().mockReturnValue(of({})), // Simule la création d'une session
    detail: jest.fn().mockReturnValue(of({})) // Simule l'obtention des détails d'une session
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };
  

  beforeEach(() => {
    // Initialisation du mock pour MatSnackBar
    mockMatSnackBar = {
      open: jest.fn(),
    } as any;

    // Configuration du module de test avec les services et modules nécessaires
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule, // Permet d'utiliser les formulaires réactifs
        RouterTestingModule, // Permet de tester la navigation dans les composants
        MatSnackBarModule // Permet d'utiliser le MatSnackBar pour afficher des notifications
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
      declarations: [FormComponent] // Déclaration du composant à tester
    });

    // Création du composant à tester
    component = TestBed.createComponent(FormComponent).componentInstance;
    router = TestBed.inject(Router); // Injection du service Router pour tester la navigation
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;
  });

  // Tests concernant l'initialisation du formulaire
  describe('Form Initialization', () => {
    it('should initialize empty form', () => {
      // Test que le formulaire est initialisé avec des valeurs vides
      component.ngOnInit();
      expect(component.sessionForm?.value).toEqual({
        name: '',
        date: '',
        teacher_id: '',
        description: ''
      });
    });

    it('should check admin rights', () => {
      // Test que l'utilisateur est redirigé si il n'a pas les droits d\'administrateur
      const navigateSpy = jest.spyOn(router, 'navigate');
      mockSessionService.sessionInformation.admin = false;
      component.ngOnInit();
      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    });

    
    
  });

  // Tests concernant l'envoi du formulaire
  describe('Form Submission', () => {
    it('should submit form in create mode', () => {
      // Test que la méthode de création est appelée lorsque le formulaire est soumis
      component.ngOnInit();
      component.sessionForm?.setValue({
        name: 'Test',
        date: '2025-01-01',
        teacher_id: '1',
        description: 'Test'
      });
      component.submit();
      expect(sessionApiService.create).toHaveBeenCalled();
    });
  });
});

// Début des tests d'intégration pour le composant FormComponent
describe('FormComponent - Integration Tests', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;

  // Simulation des services pour les tests d'intégration
  const mockSessionService = {
    sessionInformation: { admin: true }
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([ // Simule la récupération de la liste des enseignants
      { id: '1', name: 'Teacher 1' },
      { id: '2', name: 'Teacher 2' }
    ]))
  };

  const mockSessionApiService = {
    create: jest.fn().mockReturnValue(of({ // Simule la création d'une session
      id: '1',
      name: 'Test Session',
      date: '2025-01-01',
      teacher_id: '1',
      description: 'Test Description'
    })),
    detail: jest.fn().mockReturnValue(of({}))
  };

  beforeEach(async () => {
    // Configuration du module de test pour les tests d'intégration
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService }
      ],
      declarations: [FormComponent]
    }).compileComponents();

    // Création du fixture pour détecter les changements dans le DOM
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should load teachers in select input', async () => {
    // Test que les enseignants sont correctement chargés dans le champ de sélection
    const select = fixture.debugElement.nativeElement.querySelector('mat-select');
    expect(select).toBeTruthy();
    expect(mockTeacherService.all).toHaveBeenCalled();
  });

  it('should create session and navigate on successful submission', async () => {
    // Test que la session est créée et que l'utilisateur est redirigé après soumission réussie
    const navigateSpy = jest.spyOn(router, 'navigate');
    
    component.sessionForm?.setValue({
      name: 'Test Session',
      date: '2025-01-01',
      teacher_id: '1',
      description: 'Test Description'
    });

    component.submit();
    fixture.detectChanges();
    await fixture.whenStable();

    expect(mockSessionApiService.create).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });
});
