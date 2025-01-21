import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { DetailComponent } from './detail.component';

// Groupe de tests pour le composant DetailComponent
describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: SessionService;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let snackBar: MatSnackBar;
  let router: Router;

  // Mock des données utilisées dans les tests
  const mockSession = {
    id: 1,
    name: 'Test Session',
    date: new Date(),
    teacher_id: 1,
    description: 'Test Description',
    users: [1, 2, 3],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeacher = {
    id: 1,
    lastName: 'Doe',
    firstName: 'John',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    // Création de mocks pour les services et dépendances
    const mockSessionService = {
      sessionInformation: {
        admin: true,
        id: 1
      }
    };

    const mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      delete: jest.fn().mockReturnValue(of(undefined)),
      participate: jest.fn().mockReturnValue(of(undefined)),
      unParticipate: jest.fn().mockReturnValue(of(undefined))
    };

    const mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(mockTeacher))
    };

    const mockSnackBar = {
      open: jest.fn()
    };

    const mockRouter = {
      navigate: jest.fn()
    };

    const mockActivatedRoute = {
      snapshot: {
        paramMap: convertToParamMap({
          id: '1'
        })
      }
    };

    // Configuration du module de test
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    // Initialisation du composant et des dépendances
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    snackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    // Déclenche la détection des changements pour initialiser le composant
    fixture.detectChanges();
  });

  // Tests Unitaires
  describe('Unit Tests', () => {
    it('should create', () => {
      // Vérifie que le composant est correctement créé
      expect(component).toBeTruthy();
    });

    it('should call window.history.back() when back() is called', () => {
      // Vérifie que la méthode "back" retourne à la page précédente
      const spyBack = jest.spyOn(window.history, 'back');
      component.back();
      expect(spyBack).toHaveBeenCalled();
    });
  });

  // Tests d'Intégration
  describe('Integration Tests', () => {
    it('should delete session and show notification', () => {
      // Vérifie que la session est supprimée et qu'une notification est affichée
      jest.spyOn(router, 'navigate');
      jest.spyOn(sessionApiService, 'delete').mockReturnValue(of(undefined));
      jest.spyOn(snackBar, 'open');

      component.delete();

      expect(sessionApiService.delete).toHaveBeenCalledWith('1');
      expect(snackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });

    it('should handle participate action', () => {
      // Vérifie que l'action "participate" est correctement gérée
      jest.spyOn(sessionApiService, 'participate').mockReturnValue(of(undefined));
      jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));

      component.participate();

      expect(sessionApiService.participate).toHaveBeenCalledWith('1', '1');
    });

    it('should handle unParticipate action', () => {
      // Vérifie que l'action "unParticipate" est correctement gérée
      jest.spyOn(sessionApiService, 'unParticipate').mockReturnValue(of(undefined));
      jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));

      component.unParticipate();

      expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    });

    it('should load session details on init', () => {
      // Vérifie que les détails de la session sont chargés lors de l'initialisation
      jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
      jest.spyOn(teacherService, 'detail').mockReturnValue(of(mockTeacher));

      component.ngOnInit();

      expect(component.session).toEqual(mockSession);
      expect(component.teacher).toEqual(mockTeacher);
      expect(component.isParticipate).toBe(true);
    });
  });
});
