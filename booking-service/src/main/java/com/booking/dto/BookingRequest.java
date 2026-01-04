package com.booking.dto;


import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotBlank(message = "Room Id is required")
    private String roomId;

    @NotBlank(message = "Guest Name is required")
    private String guestName;

    @NotBlank(message = "Guest Email is required")
    @Email(message = "Invalid email format")
    private String guestEmail;

    @NotNull(message = "CheckIn Date is required")
    @Future(message = "CheckIn date must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "CheckOut Date is required")
    @Future(message = "CheckOut date must be in the future")
    private LocalDate checkOutDate;
}
