package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();
    }

    @Test
    void getAuthorities_ShouldReturnEmptySet() {
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void userDetails_ShouldReturnCorrectUsername() {
        assertEquals("test@test.com", userDetails.getUsername());
    }

    @Test
    void userDetails_ShouldReturnCorrectPassword() {
        assertEquals("password123", userDetails.getPassword());
    }

    @Test
    void isAccountNonExpired_ShouldReturnTrue() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldReturnTrue() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnTrue() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReturnTrue() {
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals_ShouldReturnTrue_WhenSameObject() {
        assertTrue(userDetails.equals(userDetails));
    }

    @Test
    void equals_ShouldReturnFalse_WhenNull() {
        assertFalse(userDetails.equals(null));
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentClass() {
        assertFalse(userDetails.equals(new Object()));
    }

    @Test
    void equals_ShouldReturnTrue_WhenSameId() {
        UserDetailsImpl sameIdUser = UserDetailsImpl.builder()
                .id(1L)
                .username("other@test.com")
                .build();
        assertTrue(userDetails.equals(sameIdUser));
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentId() {
        UserDetailsImpl differentIdUser = UserDetailsImpl.builder()
                .id(2L)
                .username("test@test.com")
                .build();
        assertFalse(userDetails.equals(differentIdUser));
    }
}