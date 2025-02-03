package com.openclassrooms.starterjwt.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;

@DataJpaTest
public class SessionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void save_ShouldPersistSession() {
        // Given
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("Description test");
        session.setDate(new Date());

        // When
        Session savedSession = sessionRepository.save(session);

        // Then
        assertNotNull(savedSession.getId());
        assertEquals("Yoga Session", savedSession.getName());
    }

    @Test
    void findById_ShouldReturnSession_WhenExists() {
        // Given
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDate(new Date());
        session.setDescription("Test Description"); // Ajout de la description obligatoire
        entityManager.persist(session);
        entityManager.flush();

        // When
        Optional<Session> found = sessionRepository.findById(session.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Yoga Session", found.get().getName());
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenDoesNotExist() {
        // When
        Optional<Session> found = sessionRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_ShouldRemoveSession() {
        // Given
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDate(new Date());
        session.setDescription("Test Description"); // Ajout de la description obligatoire
        entityManager.persist(session);
        entityManager.flush();
        Long id = session.getId();

        // When
        sessionRepository.deleteById(id);

        // Then
        Optional<Session> found = sessionRepository.findById(id);
        assertFalse(found.isPresent());
    }
}