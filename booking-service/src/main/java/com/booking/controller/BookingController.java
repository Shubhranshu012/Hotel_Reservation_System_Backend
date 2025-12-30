package com.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.service.BookingService;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
	@Autowired
    private BookingService bookingService;
    
    @PostMapping
    public ResponseEntity<BookingResponse> bookRoom(
        @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable String reservationId) {

    	bookingService.cancelBooking(reservationId);
        return ResponseEntity.ok().build();
    }
}
