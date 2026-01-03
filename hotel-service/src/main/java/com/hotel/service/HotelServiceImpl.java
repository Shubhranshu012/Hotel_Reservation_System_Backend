package com.hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.dto.HotelSearchRequest;
import com.hotel.dto.InventoryRequest;
import com.hotel.dto.UpdateHotelRequest;
import com.hotel.exception.BadRequestException;
import com.hotel.exception.NotFoundException;
import com.hotel.feign.BookingFeignClient;
import com.hotel.model.HSTATUS;
import com.hotel.model.Hotels;
import com.hotel.model.Room;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.RoomRepository;

@Service
public class HotelServiceImpl implements HotelService {
	@Autowired
	HotelRepository hotelRepository;
	
	@Autowired
	RoomRepository roomRepository;
	
	@Autowired
    private BookingFeignClient bookingClient;
	
	@Override
	public void createHotel(InventoryRequest request) {
		Hotels hotels = hotelRepository.findByHotelNameAndCityAndAddress(request.getHotelName(), request.getCity(),request.getAddress());
		if (hotels != null) {
			throw new BadRequestException("Hotel Exists With the Same name and Address");
		}
		Hotels hotel = Hotels.builder().hotelName(request.getHotelName()).city(request.getCity())
				.address(request.getAddress()).numberOfRooms(request.getNumberOfRooms()).status(HSTATUS.ACTIVE).booked(0).build();

		hotelRepository.save(hotel);

	}
	@Override
	public void updateHotel(String hotelId, UpdateHotelRequest request) {

	    Hotels hotel = hotelRepository.findByIdAndStatus(hotelId, HSTATUS.ACTIVE).orElseThrow(NotFoundException::new);

	    hotel.setHotelName(request.getHotelName());
	    hotel.setCity(request.getCity());
	    hotel.setAddress(request.getAddress());
	    hotel.setNumberOfRooms(request.getNumberOfRooms());	  

	    hotelRepository.save(hotel);
	}

	@Override
	public void deleteHotel(String hotelId) {

		Hotels hotel = hotelRepository.findByIdAndStatus(hotelId, HSTATUS.ACTIVE).orElseThrow(() -> new NotFoundException());

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
	
	@Override
	public List<Hotels> searchHotels(HotelSearchRequest request){

        List<Hotels> hotels = hotelRepository.findByCityAndStatus(request.getCity(),HSTATUS.ACTIVE);

        return hotels.stream()
                .filter(hotel -> {
                    List<Room> rooms = roomRepository.findByHotelId(hotel.getId());
                    List<String> bookedRoomIds =bookingClient.getBookedRooms(hotel.getId(),request.getCheckIn(),request.getCheckOut());
                    long availableRooms =rooms.stream().filter(room -> !bookedRoomIds.contains(room.getId())).count();
                    return availableRooms >= request.getRoomCount();
                })
                .collect(Collectors.toList());
    }
	@Override
    public List<Room> getAvailableRooms(String hotelId,LocalDate checkIn,LocalDate checkOut) {

        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<String> bookedRoomIds =bookingClient.getBookedRooms(hotelId, checkIn, checkOut);

        return rooms.stream()
                .filter(room -> !bookedRoomIds.contains(room.getId()))
                .collect(Collectors.toList());
    }
}
