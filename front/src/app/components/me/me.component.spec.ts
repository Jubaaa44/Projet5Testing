import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';  // Import du service MatSnackBar
import { Router } from '@angular/router';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { MeComponent } from './me.component';
import { of } from 'rxjs';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockSessionService: any;
  let mockUserService: any;
  let mockMatSnackBar: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockSessionService = {
      sessionInformation: { admin: true, id: 1 },
      logOut: jest.fn()
    };

    mockUserService = {
      getById: jest.fn().mockReturnValue(of({ id: 1, name: 'John Doe' })),
      delete: jest.fn().mockReturnValue(of({}))
    };

    mockMatSnackBar = {
      open: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },  // Correcte l'injection du service MatSnackBar
        { provide: Router, useValue: mockRouter }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Tests
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit and fetch user data', () => {
    component.ngOnInit();
    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual({ id: 1, name: 'John Doe' });
  });

  it('should call back()', () => {
    const spy = jest.spyOn(window.history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
  });

  it('should call delete()', () => {
    // Appel de la méthode delete
    component.delete();
  
    // Vérifie que la méthode delete a bien été appelée sur le service
    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    
    // Vérifie que MatSnackBar.open a bien été appelé avec les bons arguments
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      "Your account has been deleted !",
      'Close',
      { duration: 3000 }
    );

    // Vérifie que logOut est bien appelé sur le sessionService
    expect(mockSessionService.logOut).toHaveBeenCalled();

    // Vérifie que navigate est bien appelé avec le bon chemin
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
  
});
