package com.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.HotelSearchRequest;
import com.hotel.dto.InventoryRequest;
import com.hotel.dto.RoomAvailabilityRequest;
import com.hotel.dto.RoomRequest;
import com.hotel.dto.UpdateHotelRequest;
import com.hotel.dto.UpdateRoomRequest;
import com.hotel.model.Hotels;
import com.hotel.model.Room;
import com.hotel.service.HotelServiceImpl;
import com.hotel.service.RoomServiceImpl;

import jakarta.validation.Valid;

@RestController
public class HotelController {
	@Autowired
	HotelServiceImpl hotelService;
	@Autowired
	RoomServiceImpl roomService;

	//Admin (Added)
	@PostMapping("/hotel")
	public ResponseEntity<Void> addHotel(@RequestBody @Valid InventoryRequest request) {
		hotelService.createHotel(request);
		return ResponseEntity.status(201).build();
	}
	
	//Admin/Manager (Added)
	@PutMapping("/hotel/{hotelId}")
	public ResponseEntity<Void> updateHotel(@PathVariable String hotelId,@RequestBody @Valid UpdateHotelRequest request) {
	    hotelService.updateHotel(hotelId, request);
	    return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/hotel/{hotelId}")
	public ResponseEntity<Void> deleteHotel(@PathVariable String hotelId) {
	    hotelService.deleteHotel(hotelId);
	    return ResponseEntity.ok().build();
	}
	
	//Admin (Added)
	@GetMapping("/hotel/all")
	public ResponseEntity<List<Hotels>> getAllHotels() {
		List<Hotels> hotels=hotelService.getAll();
		return ResponseEntity.status(200).body(hotels);
	}
	
	//Manager (Added)
	@PostMapping("/hotel/{hotelId}/room")
	public ResponseEntity<Void> addRooms(@RequestBody @Valid List<RoomRequest> request, @PathVariable String hotelId) {
		for (Integer x = 0; x < request.size(); x++) {
			roomService.createRoom(hotelId, request.get(x));
		}
		return ResponseEntity.status(201).build();
	}
	
	//Maneger (Added)
	@PutMapping("/hotel/{hotelId}/room/{roomId}")
	public ResponseEntity<Void> updateRooms(@RequestBody @Valid UpdateRoomRequest request, @PathVariable String hotelId, @PathVariable String roomId) {
		roomService.updateRoom(hotelId, roomId, request);
		return ResponseEntity.status(200).build();
	}
	
	//internal
	@GetMapping("/hotel/{hotelId}/room/{roomId}")
	public ResponseEntity<Room> getRoom(@PathVariable String hotelId, @PathVariable String roomId) {
		Room room = roomService.getRoom(hotelId, roomId);
		return ResponseEntity.status(200).body(room);
	}
	
	
	//Public (Added)
	@PostMapping("/search")
	public ResponseEntity<List<Hotels>> searchHotels(@RequestBody @Valid HotelSearchRequest request) {
		List<Hotels> hotels=hotelService.searchHotels(request);
		return ResponseEntity.status(200).body(hotels);
	}
	//Public ( Added )
	@PostMapping("/hotel/{hotelId}/rooms/available")
    public ResponseEntity<List<Room>> getAvailableRooms(@PathVariable String hotelId,@RequestBody RoomAvailabilityRequest request) {
		List<Room> rooms= hotelService.getAvailableRooms(hotelId,request.getCheckIn(),request.getCheckOut());
        return ResponseEntity.status(200).body(rooms);
    }
}
