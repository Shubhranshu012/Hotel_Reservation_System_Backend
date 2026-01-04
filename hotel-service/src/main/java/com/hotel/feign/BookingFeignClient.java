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

@FeignClient(name = "booking-service")
public interface BookingFeignClient {

    @GetMapping("/api/booking/booked-rooms")
    List<String> getBookedRooms(@RequestParam String hotelId,@RequestParam LocalDate checkIn, @RequestParam LocalDate checkOut);
    
    @PutMapping("/api/booking/checkin/{bookingId}")
    void checkIn(@PathVariable("bookingId") String bookingId,@RequestBody CheckInRequest request);
}
