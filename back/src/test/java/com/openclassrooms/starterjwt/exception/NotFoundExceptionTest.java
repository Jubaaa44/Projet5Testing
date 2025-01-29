package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotFoundExceptionTest {

    @Test
    void shouldHaveCorrectResponseStatus() {
        // Given
        ResponseStatus responseStatus = NotFoundException.class.getAnnotation(ResponseStatus.class);

        // Then
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
    }

    @Test
    void shouldBeRuntimeException() {
        // Given
        NotFoundException exception = new NotFoundException();

        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}