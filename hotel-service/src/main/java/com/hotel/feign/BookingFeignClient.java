package com.hotel.feign;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.dto.CheckInRequest;
import com.hotel.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "booking-service", fallback = BookingServiceFallback.class)
public interface BookingFeignClient {

    @GetMapping("/api/booking/booked-rooms")
    @CircuitBreaker(name = "bookingServiceCircuitBreaker", fallbackMethod = "fallbackGetBookedRooms")
    List<String> getBookedRooms(@RequestParam String hotelId,@RequestParam LocalDate checkIn, @RequestParam LocalDate checkOut);
    
    @PutMapping("/api/booking/checkin/{bookingId}")
    void checkIn(@PathVariable("bookingId") String bookingId,@RequestBody CheckInRequest request);
    
    default List<String> fallbackGetBookedRooms(String hotelId, LocalDate checkIn, LocalDate checkOut, Throwable t) {
        throw new ServiceUnavailableException("Booking service is currently unavailable. Please try again later.");
    }
}
