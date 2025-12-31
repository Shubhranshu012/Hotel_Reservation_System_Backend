package com.hotel.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RoomAvailabilityRequest {
    private LocalDate checkIn;
    private LocalDate checkOut;
}