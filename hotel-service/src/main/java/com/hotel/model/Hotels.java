package com.hotel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotels {
	@Id
	private String HotelId; 
	private String HotelName;
	private String city;
	private String Address;
	private Integer Rating;
}
