import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        {
          provide: AuthService,
          useValue: {
            register: jest.fn()
          }
        },
        {
          provide: Router,
          useValue: {
            navigate: jest.fn()
          }
        }
      ]
    }).compileComponents();

    // Initialisation des variables communes pour chaque test
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  // === Tests Unitaires ===

  /**
   * Vérifie que le composant est correctement créé.
   */
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Vérifie que le formulaire est initialisé avec des valeurs vides.
   */
  it('should initialize the form with empty values', () => {
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  /**
   * Vérifie que le formulaire est invalide si les champs requis sont vides.
   */
  it('should mark form as invalid when required fields are missing', () => {
    component.form.patchValue({
      email: '',
      firstName: '',
      lastName: '',
      password: ''
    });
    expect(component.form.valid).toBe(false);
  });

  // === Tests d'intégration ===
  describe('Integration Tests', () => {
    /**
     * Teste le comportement lors d'une inscription réussie.
     */
    it('should handle successful registration', async () => {
      // Simule une réponse réussie de l'API
      jest.spyOn(authService, 'register').mockReturnValue(of(void 0));
      jest.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

      // Remplit le formulaire avec des données valides
      component.form.patchValue({
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
      });

      // Appelle la méthode submit
      component.submit();

      // Vérifie que les méthodes appropriées ont été appelées
      expect(authService.register).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
      expect(component.onError).toBe(false);
    });

    /**
     * Teste le comportement lors d'un échec d'inscription.
     */
    it('should handle registration failure', () => {
      // Simule une erreur retournée par l'API
      jest.spyOn(authService, 'register').mockReturnValue(
        throwError(() => new Error('Registration failed'))
      );

      // Remplit le formulaire avec des données valides
      component.form.patchValue({
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
      });

      // Appelle la méthode submit
      component.submit();

      // Vérifie que les méthodes appropriées ont été appelées
      expect(authService.register).toHaveBeenCalled();
      expect(component.onError).toBe(true);
    });

    /**
     * Teste la validation du formulaire avec des données invalides.
     */
    it('should handle form validation errors', () => {
      // Remplit le formulaire avec des données invalides
      component.form.patchValue({
        email: 'invalid-email',
        firstName: '',
        lastName: '',
        password: '12'
      });

      // Vérifie les erreurs de validation
      expect(component.form.valid).toBe(false);
      expect(component.form.get('email')?.errors?.['email']).toBeTruthy();
      expect(component.form.get('firstName')?.errors?.['required']).toBeTruthy();
      expect(component.form.get('lastName')?.errors?.['required']).toBeTruthy();
    });
  });
});
