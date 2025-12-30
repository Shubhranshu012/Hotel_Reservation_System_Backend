package com.booking.dto;


import java.time.LocalDate;

import lombok.Data;

@Data
public class BookingRequest {
    private String hotelId;
    private String roomId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
