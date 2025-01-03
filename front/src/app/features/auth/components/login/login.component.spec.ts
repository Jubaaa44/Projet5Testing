import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: any;
  let mockSessionService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockAuthService = {
      login: jest.fn()
    };

    mockSessionService = {
      logIn: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: RouterTestingModule, useValue: mockRouter }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit the form and log in the user on success', () => {
    const loginRequest = { email: 'test@example.com', password: 'password123' };
    component.form.setValue(loginRequest);
    
    mockAuthService.login.mockReturnValue(of({ userId: 1, email: 'test@example.com' }));

    component.submit();
    
    expect(mockAuthService.login).toHaveBeenCalledWith(loginRequest);
    expect(mockSessionService.logIn).toHaveBeenCalledWith({ userId: 1, email: 'test@example.com' });

    // Vérifie que la navigation a eu lieu vers "/sessions"
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set onError to true when login fails', () => {
    const loginRequest = { email: 'test@example.com', password: 'password123' };
    component.form.setValue(loginRequest);

    mockAuthService.login.mockReturnValue(throwError(() => new Error('Login failed')));
    
    component.submit();

    expect(component.onError).toBe(true);
  });

  it('should validate the form fields', () => {
    component.form.controls['email'].setValue('');
    component.form.controls['password'].setValue('');

    expect(component.form.valid).toBeFalsy();

    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('password123');

    expect(component.form.valid).toBeTruthy();
  });

  it('should call AuthService.login with correct values', () => {
    const loginRequest = { email: 'test@example.com', password: 'password123' };
    component.form.setValue(loginRequest);

    component.submit();

    expect(mockAuthService.login).toHaveBeenCalledWith(loginRequest);
  });
});
