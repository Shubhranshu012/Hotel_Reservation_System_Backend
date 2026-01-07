package com.hotel.feign;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.hotel.dto.CheckInRequest;
import com.hotel.exception.ServiceUnavailableException;

class BookingServiceFallbackTest {

    private BookingServiceFallback fallback = new BookingServiceFallback();

    @Test
    void badGetBookedRooms() {
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, () -> {
            fallback.getBookedRooms("hotel123", LocalDate.now(), LocalDate.now().plusDays(1));
        });
        assertEquals("Booking service is currently unavailable. Please try again later.", exception.getMessage());
    }

    @Test
    void badCheckIn() {
        CheckInRequest request = new CheckInRequest(); 
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, () -> {
            fallback.checkIn("booking123", request);
        });
        assertEquals("Booking service is currently unavailable. Please try again later.", exception.getMessage());
    }
}