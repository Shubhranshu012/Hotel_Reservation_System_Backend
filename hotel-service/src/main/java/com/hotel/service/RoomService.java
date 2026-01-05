package com.hotel.service;


import com.hotel.dto.CheckInRequest;
import com.hotel.dto.RoomRequest;
import com.hotel.dto.UpdateRoomRequest;
import com.hotel.model.Room;

public interface RoomService  {
	Room createRoom(String hotelId, RoomRequest request);

    Room getRoom(String hotelId, String roomId);

    void updateRoom(String hotelId, String roomId, UpdateRoomRequest request);

    void deleteRoom(String hotelId, String roomId);
    
    void CheckInCheckOut(String hotelId, String roomId,CheckInRequest request,String bookingId);
}
