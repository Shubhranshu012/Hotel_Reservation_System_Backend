package com.booking.feign;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.booking.exception.ServiceUnavailableException;

class HotelFeignClientFallbackTest {

    private HotelFeignClientFallback fallback = new HotelFeignClientFallback();

    @Test
    void errorGetRoom() {
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, () -> {
            fallback.getRoom("hotel123", "room456");
        });
        assertEquals("Hotel service is unavailable", exception.getMessage());
    }
}