package com.booking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.ChangeRequest;
import com.booking.dto.CheckInRequest;
import com.booking.model.Reservation;
import com.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
	@Autowired
    BookingService bookingService;
	
	//User/RECEPTIONIST (Added)
    @PostMapping("/{hotelId}")
    public ResponseEntity<BookingResponse> bookRoom( @RequestBody @Valid BookingRequest request,@PathVariable String hotelId) {
        BookingResponse response = bookingService.createBooking(request,hotelId);
        return ResponseEntity.status(201).body(response);
    }
    
    //User (Added)
    @DeleteMapping("/{email}/{reservationId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable String reservationId,@PathVariable String email) {

    	bookingService.cancelBooking(reservationId,email);
        return ResponseEntity.ok().build();
    }
    //User (Added)
    @GetMapping("/{email}/all")
    public ResponseEntity<List<Reservation>> getAllBooking(@PathVariable String email) {

    	List<Reservation>responce=bookingService.getAllBooking(email);
        return ResponseEntity.ok().body(responce);
    }
    
    
    
    //get all Booking for A hotel
    @GetMapping("/booking/{hotelId}")
    public ResponseEntity<List<Reservation>> getBookings(@PathVariable String hotelId) {
    	List<Reservation> responce=bookingService.getAllBookingManager(hotelId);
        return ResponseEntity.status(200).body(responce);
    }
    //User (Added)
    @PutMapping("/{email}/{reservationId}/update")
    public ResponseEntity<Void> updateBooking(@PathVariable String reservationId,@PathVariable String email,@RequestBody @Valid ChangeRequest request) {

    	bookingService.updateBooking(reservationId,email,request);
        return ResponseEntity.ok().build();
    }
    
    
    //internal
    @GetMapping("/booked-rooms")
    public List<String> getBookedRooms(@RequestParam String hotelId,@RequestParam @DateTimeFormat(pattern = "M/d/yy") LocalDate checkIn,@RequestParam @DateTimeFormat(pattern = "M/d/yy") LocalDate checkOut) {
        return bookingService.getBookedRoomIds(hotelId, checkIn, checkOut);
    }
    
    @PutMapping("/checkin/{bookingId}")
    public ResponseEntity<Void> checkInCheckOut(@PathVariable String bookingId,@RequestBody CheckInRequest request){
    	bookingService.checkInCheckOut(bookingId,request);
    	return ResponseEntity.status(200).build();
    }
    
}
