package com.openclassrooms.starterjwt.security.base;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.openclassrooms.starterjwt.security.WebSecurityConfig;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthEntryPointJwt unauthorizedHandler;

    @InjectMocks
    private WebSecurityConfig webSecurityConfig;

    @Test
    void authenticationJwtTokenFilter_ShouldReturnAuthTokenFilter() {
        // When
        AuthTokenFilter filter = webSecurityConfig.authenticationJwtTokenFilter();

        // Then
        assertNotNull(filter);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // When
        PasswordEncoder encoder = webSecurityConfig.passwordEncoder();

        // Then
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void configure_ShouldWork() throws Exception {
        // Given
        AuthenticationManagerBuilder auth = new AuthenticationManagerBuilder(mock(ObjectPostProcessor.class));
        
        // When
        webSecurityConfig.configure(auth);
        
        // Then
        // Si aucune exception n'est lancée, le test passe
        assertTrue(true);
    }
}