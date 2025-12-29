package com.hotel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotels {
	@Id
	private String id;
	private String hotelName;
	private String city;
	private String address;
	private Integer numberOfRooms;
	private HSTATUS status;
}
