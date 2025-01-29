package com.openclassrooms.starterjwt.services;

//Imports existants
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;

//Imports pour JUnit et Mockito
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//Imports utilitaires
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

//Imports pour les assertions
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private SessionService sessionService;
    
    private Session session;
    private User user;
    private Teacher teacher;
    
    @BeforeEach
    void setUp() {
        // Initialisation du Teacher
        teacher = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .build();

        // Initialisation du User avec tous les champs obligatoires
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("User")
                .lastName("Test")
                .password("password123")  // Ajout du password obligatoire
                .admin(false)            // Ajout du champ admin
                .build();

        // Initialisation de la Session
        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .description("Description of yoga session")
                .date(new Date())
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();
    }
    
    @Test
    void create_ShouldReturnCreatedSession() {
        // Given
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        // When
        Session result = sessionService.create(session);
        
        // Then
        assertNotNull(result);
        assertEquals(session.getName(), result.getName());
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void getById_ShouldReturnSession_WhenExists() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        // When
        Session result = sessionService.getById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(session.getId(), result.getId());
    }

    @Test
    void getById_ShouldReturnNull_WhenNotExists() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Session result = sessionService.getById(1L);
        
        // Then
        assertNull(result);
    }

    @Test
    void update_ShouldReturnUpdatedSession() {
        // Given
        Session updatedSession = Session.builder()
                .id(1L)
                .name("Updated Session")
                .description("Updated description")
                .date(new Date())
                .teacher(teacher)
                .build();
        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);
        
        // When
        Session result = sessionService.update(1L, updatedSession);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Session", result.getName());
    }
    
    @Test
    void participate_ShouldAddUserToSession() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        // When
        sessionService.participate(1L, 1L);
        
        // Then
        verify(sessionRepository).save(session);
        assertTrue(session.getUsers().contains(user));
    }
    
    @Test
    void participate_ShouldThrowNotFoundException_WhenSessionNotFound() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 1L);
        });
    }
    
    @Test
    void participate_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 1L);
        });
    }

    @Test
    void noLongerParticipate_ShouldRemoveUserFromSession() {
        // Given
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        // When
        sessionService.noLongerParticipate(1L, 1L);
        
        // Then
        verify(sessionRepository).save(session);
        assertFalse(session.getUsers().contains(user));
    }

    @Test
    void noLongerParticipate_ShouldThrowBadRequestException_WhenUserNotParticipating() {
        // Given
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        
        // When & Then
        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(1L, 1L);
        });
    }
}