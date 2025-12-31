package com.hotel.service;

import java.time.LocalDate;
import java.util.List;

import com.hotel.dto.HotelSearchRequest;
import com.hotel.dto.InventoryRequest;
import com.hotel.dto.UpdateHotelRequest;
import com.hotel.model.Hotels;
import com.hotel.model.Room;

public interface HotelService {
	void createHotel(InventoryRequest request);
	public void updateHotel(String hotelId, UpdateHotelRequest request);
    void deleteHotel(String hotelId);
    public List<Hotels> searchHotels(HotelSearchRequest request);
    public List<Room> getAvailableRooms(String hotelId,LocalDate checkIn,LocalDate checkOut);
}
