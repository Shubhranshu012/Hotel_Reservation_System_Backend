package com.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.CheckInRequest;
import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomService;

@RestController
public class RoomController {
	@Autowired
	RoomRepository roomRepository;
	
	@Autowired
	RoomService roomService;
	
	@GetMapping("rooms/{hotelId}")
	public ResponseEntity<List<Room>> getAllRooms(@PathVariable String hotelId){
		List<Room> rooms=roomRepository.findByHotelId(hotelId);
		return ResponseEntity.status(200).body(rooms);
	}
	
	@PutMapping("{hotelId}/rooms/{roomId}")
	public ResponseEntity<Void> checkIncheckOut(@PathVariable String hotelId,@PathVariable String roomId,@RequestBody CheckInRequest checkInRequest){
		roomService.CheckInCheckOut(hotelId, roomId, checkInRequest);
		return ResponseEntity.status(200).build();
	}
}
