package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadRequestExceptionTest {

    @Test
    void shouldHaveCorrectResponseStatus() {
        // Given
        ResponseStatus responseStatus = BadRequestException.class.getAnnotation(ResponseStatus.class);

        // Then
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
    }

    @Test
    void shouldBeRuntimeException() {
        // Given
        BadRequestException exception = new BadRequestException();

        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}