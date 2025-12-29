package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hotel.model.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room,String>{
	Optional<Room> findByIdAndHotelId(String id,String hotelId);
	List<Room> findByHotelId(String hotelId);
	Boolean existsByHotelIdAndRoomNumber(String hotelId, String roomNumber);
}
