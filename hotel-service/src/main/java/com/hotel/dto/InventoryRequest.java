package com.hotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequest {

    @NotNull(message = "Hotel name is required")
    private String hotelName;

    @NotNull(message="City is required")
    private String city;

    @NotNull(message="Address is required")
    private String address;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 0, message = "Number of rooms must be 0 or greater")
    private Integer numberOfRooms;
}
