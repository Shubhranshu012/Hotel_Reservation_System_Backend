package com.hotel.feign;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.hotel.exception.ServiceUnavailableException;

class BookingFeignClientTest {

    private BookingFeignClient client = new BookingFeignClient() {
        @Override
        public java.util.List<String> getBookedRooms(String hotelId, LocalDate checkIn, LocalDate checkOut) {
            return null; 
        }
        @Override
        public void checkIn(String bookingId, com.hotel.dto.CheckInRequest request) {
            
        }
    };

    @Test
    void fallbackGetBookedRooms() {

        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, () -> {
            client.fallbackGetBookedRooms("hotel123", LocalDate.now(), LocalDate.now().plusDays(1), new Throwable("Test"));
        });
        assertEquals("Booking service is currently unavailable. Please try again later.", exception.getMessage());
    }
}