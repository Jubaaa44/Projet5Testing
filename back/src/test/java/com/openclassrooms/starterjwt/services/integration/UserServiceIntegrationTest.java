package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.models.User;
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
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        // Nettoyage
        userRepository.deleteAll();

        // Création d'un user
        user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setAdmin(false);
        user = userRepository.save(user);
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // When
        User result = userService.findById(user.getId());

        // Then
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void findById_ShouldReturnNull_WhenUserDoesNotExist() {
        // When
        User result = userService.findById(999L);

        // Then
        assertNull(result);
    }

    @Test
    void delete_ShouldRemoveUser() {
        // When
        userService.delete(user.getId());

        // Then
        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    void delete_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        userRepository.deleteAll(); // Nettoyer la base d'abord
        
        // Créer et sauvegarder un nouvel utilisateur normal (non admin)
        User normalUser = new User();
        normalUser.setEmail("normal@test.com");
        normalUser.setFirstName("Normal");
        normalUser.setLastName("User");
        normalUser.setPassword(passwordEncoder.encode("password123"));
        normalUser.setAdmin(false);
        userRepository.save(normalUser);
        
        Long nonExistentId = 999L;
        
        // When & Then
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userService.delete(nonExistentId);
        });
        
        // Vérifier que l'utilisateur normal existe toujours
        assertTrue(userRepository.existsById(normalUser.getId()));
    }
}