package com.hotel.service;

import java.util.List;

import com.hotel.dto.HotelSearchRequest;
import com.hotel.dto.InventoryRequest;
import com.hotel.model.Hotels;

public interface HotelService {
	void createHotel(InventoryRequest request);
    void deleteHotel(String hotelId);
    public List<Hotels> searchHotels(HotelSearchRequest request);
}
