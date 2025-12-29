package com.hotel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Room_Catagory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room_Catagory {
	@Id
	private String id;
	private String Hotel_id;
	private RTYPE Type;
	private Integer capacity;
	private float price;

}
