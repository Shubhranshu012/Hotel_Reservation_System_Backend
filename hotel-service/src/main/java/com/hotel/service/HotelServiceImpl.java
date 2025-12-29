package com.hotel.service;


import org.springframework.beans.factory.annotation.Autowired;
import com.hotel.dto.InventoryRequest;
import com.hotel.exception.HotelNotFoundException;
import com.hotel.model.HSTATUS;
import com.hotel.model.Hotels;
import com.hotel.repository.HotelRepository;

public class HotelServiceImpl implements HotelService {
	@Autowired
	HotelRepository hotelRepository;
	@Override
    public void createHotel(InventoryRequest request) {

        Hotels hotel = Hotels.builder()
                .hotelName(request.getHotelName())
                .city(request.getCity())
                .address(request.getAddress())
                .numberOfRooms(request.getNumberOfRooms())
                .status(HSTATUS.ACTIVE)
                .build();
        
        hotelRepository.save(hotel);
        
    }
	 @Override
	    public void deleteHotel(Long hotelId) {

	        Hotels hotel = hotelRepository
	                .findByIdAndStatus(hotelId, HSTATUS.ACTIVE)
	                .orElseThrow(() -> new HotelNotFoundException("Active hotel not found"));

	        hotel.setStatus(HSTATUS.INACTIVE);
	        hotelRepository.save(hotel);
	    }
}
