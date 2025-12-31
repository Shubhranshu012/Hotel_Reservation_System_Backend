package com.hotel.feign;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service", url = "http://localhost:8003")
public interface BookingFeignClient {

    @GetMapping("/api/booking/booked-rooms")
    List<String> getBookedRooms(
            @RequestParam String hotelId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut
    );
}
