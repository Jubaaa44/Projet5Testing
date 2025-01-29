/*package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

public class AuthEntryPointJwtTest {
    
    @Test
    void commence_ShouldReturnUnauthorizedError() throws Exception {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(byteArrayOutputStream, true);
        
        when(response.getWriter()).thenReturn(writer);
        when(request.getServletPath()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Unauthorized error message");
        
        // When
        AuthEntryPointJwt authEntryPointJwt = new AuthEntryPointJwt();
        authEntryPointJwt.commence(request, response, authException);
        writer.flush();
        
        // Then
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String jsonResponse = byteArrayOutputStream.toString();
        assertTrue(jsonResponse.contains("\"status\":401"));
        assertTrue(jsonResponse.contains("\"error\":\"Unauthorized\""));
        assertTrue(jsonResponse.contains("\"message\":\"Unauthorized error message\""));
        assertTrue(jsonResponse.contains("\"/api/test\""));
    }
}
*/