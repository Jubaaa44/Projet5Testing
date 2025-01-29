package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Test
    void doFilterInternal_WithValidJWT_ShouldSetAuthentication() throws Exception {
        // Given
        String token = "valid.jwt.token";
        UserDetails userDetails = UserDetailsImpl.builder()
                .username("test@test.com")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername("test@test.com");
    }

    @Test
    void doFilterInternal_WithInvalidJWT_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(jwtUtils.validateJwtToken("invalid.token")).thenReturn(false);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void doFilterInternal_WithInvalidAuthorizationHeader_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void doFilterInternal_WithException_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(jwtUtils.validateJwtToken(any())).thenThrow(new RuntimeException("Test exception"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }
}