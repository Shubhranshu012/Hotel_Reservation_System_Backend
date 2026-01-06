package com.hotel.dto;


import com.hotel.model.RSTATUS;
import com.hotel.model.RTYPE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponce {
    private String id;
    private String roomNumber;
    private String hotelName;
    private String hotelId;
    private RSTATUS status;  
    private RTYPE type;
    private Double price;
}
