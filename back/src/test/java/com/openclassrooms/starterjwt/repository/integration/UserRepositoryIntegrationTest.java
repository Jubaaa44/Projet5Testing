package com.openclassrooms.starterjwt.repository.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
	    "spring.datasource.url=jdbc:h2:mem:testdb_user_repo;DB_CLOSE_ON_EXIT=FALSE",
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
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Given
        userRepository.deleteAll();
            
        // S'assurer que l'utilisateur a toutes les propriétés requises
        user = new User();
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setAdmin(false);
        user = userRepository.save(user);
    }

    @Test
    void existsByEmail_ShouldHandleMultipleUsers() {
        // Given
        User secondUser = new User();
        secondUser.setEmail("test2@test.com");  // Email unique
        secondUser.setFirstName("Jane");
        secondUser.setLastName("Smith");
        secondUser.setPassword("password456");
        secondUser.setAdmin(false);
        userRepository.save(secondUser);

        // When & Then
        assertTrue(userRepository.existsByEmail("test@test.com"));
        assertTrue(userRepository.existsByEmail("test2@test.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@test.com"));
    }

    @Test
    void findByEmail_ShouldHandleCaseSensitivity() {
        // When
        Optional<User> foundLowerCase = userRepository.findByEmail("test@test.com");
        Optional<User> foundUpperCase = userRepository.findByEmail("TEST@TEST.COM");
        
        // Then
        assertTrue(foundLowerCase.isPresent());
        assertFalse(foundUpperCase.isPresent());
    }

    @Test
    void persistUser_ShouldSetIdAndMaintainData() {
        // Given
        userRepository.deleteAll(); // S'assurer que la base est vide

        User newUser = new User();
        newUser.setEmail("unique" + System.currentTimeMillis() + "@test.com"); // Email unique
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPassword("newpass");
        newUser.setAdmin(true);

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertNotNull(savedUser.getId());
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        
        User retrievedUser = foundUser.get();
        assertEquals(newUser.getEmail(), retrievedUser.getEmail());
        assertEquals(newUser.getFirstName(), retrievedUser.getFirstName());
        assertEquals(newUser.getLastName(), retrievedUser.getLastName());
        assertEquals(newUser.getPassword(), retrievedUser.getPassword());
        assertTrue(retrievedUser.isAdmin());
    }
}