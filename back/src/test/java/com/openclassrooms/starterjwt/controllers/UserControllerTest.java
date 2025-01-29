package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Initialize User
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setAdmin(false);

        // Initialize UserDto
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        // Setup Security Context
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void findById_ShouldReturnUser() {
        // Given
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        ResponseEntity<?> response = userController.findById("1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<?> response = userController.findById("1");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsNotNumeric() {
        // When
        ResponseEntity<?> response = userController.findById("invalid");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void delete_ShouldReturnOk_WhenUserExistsAndIsAuthorized() {
        // Given
        when(userService.findById(1L)).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@test.com");

        // When
        ResponseEntity<?> response = userController.save("1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService).delete(1L);
    }

    @Test
    void delete_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() {
        // Given
        when(userService.findById(1L)).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("other@test.com");

        // When
        ResponseEntity<?> response = userController.save("1");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<?> response = userController.save("1");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsNotNumeric() {
        // When
        ResponseEntity<?> response = userController.save("invalid");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, never()).delete(anyLong());
    }
}