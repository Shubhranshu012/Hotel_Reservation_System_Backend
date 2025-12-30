package com.booking.dto;


import lombok.Data;

@Data
public class RoomResponse {

    private String id;
    private String roomNumber;
    private String hotelId;
    private String status; 
    private String type;    
    private Double price;
}
