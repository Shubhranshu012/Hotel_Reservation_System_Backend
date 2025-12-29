package com.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.InventoryRequest;
import com.hotel.dto.RoomRequest;
import com.hotel.dto.UpdateRoomRequest;
import com.hotel.model.Hotels;
import com.hotel.service.HotelServiceImpl;
import com.hotel.service.RoomServiceImpl;

@RestController
public class HotelController {
	@Autowired
	HotelServiceImpl hotelService;
	@Autowired
	RoomServiceImpl roomService;
	@PostMapping("/hotel")
	private ResponseEntity<Void> addHotel(@RequestBody InventoryRequest request){
		hotelService.createHotel(request);
		return ResponseEntity.status(201).build();
	}
	@PostMapping("/hotel/{hotelId}/room")
	private ResponseEntity<Void> addRooms(@RequestBody List<RoomRequest> request,@PathVariable String hotelId) {
		for(Integer x=0;x<request.size();x++) {
			roomService.createRoom(hotelId, request.get(x));
		}
	    return ResponseEntity.status(201).build();
	}
	
	@PutMapping("/hotel/{hotelId}")
	private ResponseEntity<Void> updateHotel(@RequestBody InventoryRequest requests,@PathVariable String hotelId) {
	    return ResponseEntity.status(201).build();
	}
	@GetMapping("/all")
	private ResponseEntity<List<Hotels>> getAllHotels(@RequestBody List<RoomRequest> requests) {
	    return ResponseEntity.status(201).build();
	}
	@PutMapping("/hotel/{hotelId}/room/{roomId}")
	private ResponseEntity<Void> updateRooms(@RequestBody UpdateRoomRequest request,@PathVariable String hotelId,@PathVariable String roomId) {
		roomService.updateRoom(hotelId, roomId, request);
	    return ResponseEntity.status(201).build();
	}
}
