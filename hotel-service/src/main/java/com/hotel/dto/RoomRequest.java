package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.hotel.model.RSTATUS;
import com.hotel.model.RTYPE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Hotel ID is required")
    private String hotelId;

    @NotNull(message = "Room status is required")
    private RSTATUS status;

    @NotNull(message = "Room type is required")
    private RTYPE type;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;
}
