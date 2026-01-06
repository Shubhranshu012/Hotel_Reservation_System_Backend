package com.booking.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingEvent {

    private String eventType;        // BOOKING_CONFIRMED / BOOKING_CANCELLED / CHECK_IN_REMINDER / CHECK_OUT_REMINDER
    private String reservationId;
    private String hotelId;
    private String roomId;
    private String guestEmail;
    private String roomCategory;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String checkIn;
    private String checkOut;
}