package com.hotel.dto;


import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateHotelRequest {

    private String hotelName;
    private String city;
    private String address;

    @Min(value = 0, message = "Number of rooms must be 0 or greater")
    private Integer numberOfRooms;
}
