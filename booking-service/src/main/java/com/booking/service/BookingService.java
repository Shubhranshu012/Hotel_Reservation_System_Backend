package com.booking.service;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;

public interface BookingService {
	BookingResponse createBooking(BookingRequest request);
    void cancelBooking(String reservationId);
}
