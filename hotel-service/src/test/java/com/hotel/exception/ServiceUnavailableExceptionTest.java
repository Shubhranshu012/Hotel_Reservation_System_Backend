package com.hotel.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServiceUnavailableExceptionTest {

    @Test
    void constructor() {
        String message = "Service is down";
        ServiceUnavailableException exception = new ServiceUnavailableException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void nullConstructor() {
        ServiceUnavailableException exception = new ServiceUnavailableException(null);
        assertNull(exception.getMessage());
    }
}