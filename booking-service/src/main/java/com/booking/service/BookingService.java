package com.booking.service;

import java.time.LocalDate;
import java.util.List;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;

public interface BookingService {
	public BookingResponse createBooking(BookingRequest request,String hotelId);
    void cancelBooking(String reservationId);
    List<String> getBookedRoomIds(String hotelId,LocalDate checkIn,LocalDate checkOut);
}
