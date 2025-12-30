package com.hotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.dto.HotelSearchRequest;
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
		Hotels hotels = hotelRepository.findByHotelNameAndCityAndAddress(request.getHotelName(), request.getCity(),
				request.getAddress());
		if (hotels != null) {
			throw new BadRequestException("Hotel Exists With the Same name and Address");
		}
		Hotels hotel = Hotels.builder().hotelName(request.getHotelName()).city(request.getCity())
				.address(request.getAddress()).numberOfRooms(request.getNumberOfRooms()).status(HSTATUS.ACTIVE).booked(0).build();

		hotelRepository.save(hotel);

	}

	@Override
	public void deleteHotel(String hotelId) {

		Hotels hotel = hotelRepository.findByIdAndStatus(hotelId, HSTATUS.ACTIVE)
				.orElseThrow(() -> new NotFoundException());

		hotel.setStatus(HSTATUS.INACTIVE);
		hotelRepository.save(hotel);
	}

	public List<Hotels> getAll() {
		List<Hotels> hotels = hotelRepository.findAll();
		if (hotels.isEmpty()) {
			throw new NotFoundException();
		}
		return hotels;
	}
	public List<Hotels> searchHotels(HotelSearchRequest request) {

	    int requiredRooms = request.getRooms();
	    List<Hotels> hotels =hotelRepository.findByCityAndStatus(request.getCity(), HSTATUS.ACTIVE);

	    return hotels.stream()
	            .filter(hotel -> {
	                int availableRooms = hotel.getNumberOfRooms() - hotel.getBooked();
	                return availableRooms >= requiredRooms;
	            })
	            .toList();
	}

}
