package com.booking.dto;

import lombok.Data;

@Data
public class BookingEvent {

    private String eventType;        // BOOKING_CONFIRMED / BOOKING_CANCELLED
    private String reservationId;
    private String hotelId;
    private String roomId;
    private String guestEmail;
    private String checkIn;
    private String checkOut;
}