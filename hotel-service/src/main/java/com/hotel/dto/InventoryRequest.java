package com.hotel.dto;

import lombok.Data;

@Data
public class InventoryRequest {
	
	private String HotelName;
	private String city;
	private String Address;
	private Integer Rating;
}
