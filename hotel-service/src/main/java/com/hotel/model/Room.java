package com.hotel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
	@Id
	private String id;
	private String room_number;
	private RSTATUS status;
	private String hotel_id;
	private String category_id;
}
