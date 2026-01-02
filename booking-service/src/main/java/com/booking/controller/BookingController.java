package com.booking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;
import com.booking.service.BookingService;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
	@Autowired
    private BookingService bookingService;
    
	@Autowired
	ReservationRepository reservationRepository;
	
	//User/RECEPTIONIST (Added)
    @PostMapping("/{hotelId}")
    public ResponseEntity<BookingResponse> bookRoom( @RequestBody BookingRequest request,@PathVariable String hotelId) {
        BookingResponse response = bookingService.createBooking(request,hotelId);
        return ResponseEntity.status(201).body(response);
    }
    
    //User (Added)
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable String reservationId) {

    	bookingService.cancelBooking(reservationId);
        return ResponseEntity.ok().build();
    }
    
    //internal
    @GetMapping("/booked-rooms")
    public List<String> getBookedRooms(@RequestParam String hotelId,@RequestParam @DateTimeFormat(pattern = "M/d/yy") LocalDate checkIn,@RequestParam @DateTimeFormat(pattern = "M/d/yy") LocalDate checkOut) {
        return bookingService.getBookedRoomIds(hotelId, checkIn, checkOut);
    }
    
    //
    @GetMapping("/booked-rooms/{hotelId}")
    public ResponseEntity<List<Reservation>> getBookings(@PathVariable String hotelId) {
    	List<Reservation> responce=reservationRepository.findByHotelId(hotelId);
        return ResponseEntity.status(200).body(responce);
    }
    
}
