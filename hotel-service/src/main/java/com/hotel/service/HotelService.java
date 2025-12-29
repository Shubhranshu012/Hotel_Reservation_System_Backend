package com.hotel.service;

import com.hotel.dto.InventoryRequest;

public interface HotelService {
	void createHotel(InventoryRequest request);
    void deleteHotel(String hotelId);
}
