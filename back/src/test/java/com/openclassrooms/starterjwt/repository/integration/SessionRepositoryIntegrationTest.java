package com.openclassrooms.starterjwt.repository.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import javax.transaction.Transactional;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

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
@Transactional
public class SessionRepositoryIntegrationTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Session session;
    private Teacher teacher;
    private User user;

    @BeforeEach
    void setUp() {
        // Création du teacher
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        // Création du user
        user = new User();
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user = userRepository.save(user);

        // Création de la session
        session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Test Description");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>());
        session = sessionRepository.save(session);
    }

    @Test
    void shouldSaveSessionWithTeacher() {
        // When
        Session foundSession = sessionRepository.findById(session.getId()).orElse(null);

        // Then
        assertNotNull(foundSession);
        assertNotNull(foundSession.getTeacher());
        assertEquals(teacher.getId(), foundSession.getTeacher().getId());
    }

    @Test
    void shouldSaveSessionWithUsers() {
        // Given
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        // When
        Session foundSession = sessionRepository.findById(session.getId()).orElse(null);

        // Then
        assertNotNull(foundSession);
        assertEquals(1, foundSession.getUsers().size());
        assertEquals(user.getId(), foundSession.getUsers().get(0).getId());
    }

    @Test
    void shouldDeleteSessionWithoutAffectingTeacherAndUsers() {
        // Given
        session.getUsers().add(user);
        session = sessionRepository.save(session);
        Long sessionId = session.getId();
        Long teacherId = teacher.getId();
        Long userId = user.getId();

        // When
        sessionRepository.deleteById(sessionId);

        // Then
        assertFalse(sessionRepository.findById(sessionId).isPresent());
        assertTrue(teacherRepository.findById(teacherId).isPresent());
        assertTrue(userRepository.findById(userId).isPresent());
    }

    @Test
    void shouldUpdateSessionFields() {
        // Given
        String newName = "Updated Session";
        session.setName(newName);

        // When
        Session updatedSession = sessionRepository.save(session);

        // Then
        assertEquals(newName, updatedSession.getName());
        assertEquals(session.getId(), updatedSession.getId());
    }

    @Test
    void shouldFindAllSessions() {
        // Given
        sessionRepository.deleteAll(); // S'assurer que la base est vide

        // Créer deux sessions pour le test
        Session session1 = new Session();
        session1.setName("Session 1");
        session1.setDate(new Date());
        session1.setDescription("Description 1");
        session1.setTeacher(teacher);
        sessionRepository.save(session1);

        Session session2 = new Session();
        session2.setName("Session 2");
        session2.setDate(new Date());
        session2.setDescription("Description 2");
        session2.setTeacher(teacher);
        sessionRepository.save(session2);

        // When
        List<Session> sessions = sessionRepository.findAll();

        // Then
        assertEquals(2, sessions.size(), "Should find exactly two sessions");
        assertTrue(sessions.stream().anyMatch(s -> s.getName().equals("Session 1")));
        assertTrue(sessions.stream().anyMatch(s -> s.getName().equals("Session 2")));
    }
}