package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Configuration des propriétés JWT
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "yourSecretKey");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000); // 24 heures

        // Initialisation de UserDetails
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .build();
    }

    @Test
    void generateJwtToken_ShouldGenerateValidToken() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(userDetails.getUsername(), jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalidToken";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForExpiredToken() {
        // Given
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -86400000); // -24 heures (déjà expiré)
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String expiredToken = jwtUtils.generateJwtToken(authentication);
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForEmptyToken() {
        // When
        boolean isValid = jwtUtils.validateJwtToken("");

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForNullToken() {
        // When
        boolean isValid = jwtUtils.validateJwtToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getUserNameFromJwtToken_ShouldReturnCorrectUsername() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertEquals(userDetails.getUsername(), username);
    }
}