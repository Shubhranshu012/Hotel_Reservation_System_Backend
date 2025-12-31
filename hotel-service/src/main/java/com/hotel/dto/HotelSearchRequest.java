package com.hotel.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelSearchRequest {

    @NotBlank(message = "City is required")
    private String city;
    
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;
    
    @NotNull(message = "Number of room is required")
    @Min(value = 1, message = "Room must be at least 1")
    private Integer roomCount;
}

