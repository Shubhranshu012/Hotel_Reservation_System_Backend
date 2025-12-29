package com.hotel.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.dto.InventoryRequest;
import com.hotel.exception.BadRequestException;
import com.hotel.exception.NotFoundException;
import com.hotel.model.HSTATUS;
import com.hotel.model.Hotels;
import com.hotel.repository.HotelRepository;

@Service
public class HotelServiceImpl implements HotelService {
	@Autowired
	HotelRepository hotelRepository;
	@Override
    public void createHotel(InventoryRequest request) {
		Hotels hotels=hotelRepository.findByHotelNameAndCityAndAddress(request.getHotelName(),request.getCity(),request.getAddress());
		if(hotels != null) {
			throw new BadRequestException("Hotel Exists With the Same name and Address");
		}
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
	    public void deleteHotel(String hotelId) {

	        Hotels hotel = hotelRepository
	                .findByIdAndStatus(hotelId, HSTATUS.ACTIVE)
	                .orElseThrow(() -> new NotFoundException());

	        hotel.setStatus(HSTATUS.INACTIVE);
	        hotelRepository.save(hotel);
	    }
}
