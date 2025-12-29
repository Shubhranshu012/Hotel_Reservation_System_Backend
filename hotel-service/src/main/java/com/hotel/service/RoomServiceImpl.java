package com.hotel.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.dto.RoomRequest;
import com.hotel.dto.UpdateRoomRequest;
import com.hotel.exception.BadRequestException;
import com.hotel.exception.NotFoundException;
import com.hotel.model.HSTATUS;
import com.hotel.model.Hotels;
import com.hotel.model.RSTATUS;
import com.hotel.model.Room;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {
	@Autowired
	RoomRepository roomRepository;
	@Autowired
	HotelRepository hotelRepository;

	@Override
	public Room createRoom(String hotelId, RoomRequest request) {

		validateActiveHotel(hotelId);

		if (roomRepository.existsByHotelIdAndRoomNumber(hotelId, request.getRoomNumber())) {
			throw new BadRequestException("Room number already exists for this hotel");
		}

		Room room = Room.builder().hotelId(hotelId).roomNumber(request.getRoomNumber()).type(request.getType())
				.status(RSTATUS.AVAILABLE).build();

		return roomRepository.save(room);
	}

	@Override
	public List<Room> getRoomsByHotel(String hotelId) {
		return roomRepository.findByHotelId(hotelId);
	}

	@Override
	public void updateRoom(String hotelId, String roomId, UpdateRoomRequest request) {

		validateActiveHotel(hotelId);

		Room room = getRoom(hotelId, roomId);
		if (room.getStatus() == RSTATUS.OCCUPIED) {
			throw new BadRequestException("Cannot update an occupied room");
		}
		room.setPrice(request.getPrice());
		room.setStatus(request.getStatus());
		room.setType(request.getType());
	}

	@Override
	public void deleteRoom(String hotelId, String roomId) {

		validateActiveHotel(hotelId);
		Room room = getRoom(hotelId, roomId);

		if (room.getStatus() == RSTATUS.OCCUPIED) {
			throw new BadRequestException("Cannot delete an occupied room");
		}

		roomRepository.delete(room);
	}

	private Hotels validateActiveHotel(String hotelId) {
	    return hotelRepository
	            .findByIdAndStatus(hotelId, HSTATUS.ACTIVE)
	            .orElseThrow(() ->
	                    new BadRequestException("Hotel not found or inactive")
	            );
	}
	@Override
	public Room getRoom(String hotelId, String roomId) {
		return roomRepository.findByIdAndHotelId(roomId, hotelId)
				.orElseThrow(() -> new NotFoundException());
	}
}
