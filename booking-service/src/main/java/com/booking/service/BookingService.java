package com.booking.service;

import java.time.LocalDate;
import java.util.List;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.ChangeRequest;
import com.booking.dto.CheckInRequest;
import com.booking.model.Reservation;

public interface BookingService {
	public BookingResponse createBooking(BookingRequest request,String hotelId);
    public void cancelBooking(String reservationId,String email);
    public List<String> getBookedRoomIds(String hotelId,LocalDate checkIn,LocalDate checkOut);
    public List<Reservation> getAllBooking(String email);
    public void updateBooking(String reservationId, String email, ChangeRequest request);
    public void checkInCheckOut(String reservationId,CheckInRequest checkInRequest);
    public List<Reservation> getAllBookingManager(String hotelId);
}
