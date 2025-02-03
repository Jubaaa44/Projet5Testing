package com.openclassrooms.starterjwt.services.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "oc.app.jwtSecret=bezKoderSecretKey",
    "oc.app.jwtExpirationMs=86400000",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.main.allow-circular-references=true"
})
class SessionServiceIntegrationTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Session session;
    private User user;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // Nettoyage
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        // Création d'un teacher
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        // Création d'un user avec tous les champs requis
        user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user = userRepository.save(user);

        // Création d'une session
        session = new Session();
        session.setName("Test Session");
        session.setDate(new Date());
        session.setDescription("Test Description");
        session.setTeacher(teacher);
        session = sessionRepository.save(session);
    }

    @Test
    void createSession_ShouldSaveAndReturnSession() {
        // Given
        Session newSession = new Session();
        newSession.setName("New Session");
        newSession.setDate(new Date());
        newSession.setDescription("New Description");
        newSession.setTeacher(teacher);

        // When
        Session result = sessionService.create(newSession);

        // Then
        assertNotNull(result.getId());
        assertEquals("New Session", result.getName());
    }

    @Test
    void getById_ShouldReturnSession() {
        // When
        Session result = sessionService.getById(session.getId());

        // Then
        assertNotNull(result);
        assertEquals(session.getName(), result.getName());
    }

    @Test
    void findAll_ShouldReturnAllSessions() {
        // When
        List<Session> result = sessionService.findAll();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void participate_ShouldAddUserToSession() {
        // When
        sessionService.participate(session.getId(), user.getId());

        // Then
        Session updatedSession = sessionService.getById(session.getId());
        assertTrue(updatedSession.getUsers().contains(user));
    }

    @Test
    void participate_ShouldThrowNotFoundException_WhenSessionNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(999L, user.getId());
        });
    }

    @Test
    void participate_ShouldThrowNotFoundException_WhenUserNotFound() {
        // When & Then
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(session.getId(), 999L);
        });
    }

    @Test
    void noLongerParticipate_ShouldRemoveUserFromSession() {
        // Given
        sessionService.participate(session.getId(), user.getId());

        // When
        sessionService.noLongerParticipate(session.getId(), user.getId());

        // Then
        Session updatedSession = sessionService.getById(session.getId());
        assertFalse(updatedSession.getUsers().contains(user));
    }

    @Test
    void noLongerParticipate_ShouldThrowBadRequestException_WhenUserNotParticipating() {
        // When & Then
        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(session.getId(), user.getId());
        });
    }
}