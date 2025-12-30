package com.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    
    @PostMapping
    public ResponseEntity<BookingResponse> bookRoom(
        @RequestBody BookingRequest request) {
        BookingResponse response;
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable String reservationId) {

        return ResponseEntity.ok().build();
    }
}
