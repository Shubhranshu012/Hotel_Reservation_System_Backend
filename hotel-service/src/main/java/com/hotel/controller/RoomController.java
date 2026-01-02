package com.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;

@RestController
public class RoomController {
	@Autowired
	RoomRepository roomRepository;
	
	@GetMapping("rooms/{hotelId}")
	public ResponseEntity<List<Room>> getAllRooms(@PathVariable String hotelId){
		List<Room> rooms=roomRepository.findByHotelId(hotelId);
		return ResponseEntity.status(200).body(rooms);
	}
}
