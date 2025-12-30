package com.booking.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.RoomResponse;
import com.booking.feign.HotelFeignClient;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private ReservationRepository repository;

	@Autowired
	private HotelFeignClient hotelClient;

	@Override
	public BookingResponse createBooking(BookingRequest request) {

		RoomResponse room = hotelClient.getRoom(request.getHotelId(), request.getRoomId());

		if (!"AVAILABLE".equals(room.getStatus())) {
			throw new RuntimeException("Room not available");
		}

		Reservation reservation = new Reservation();
		reservation.setHotelId(request.getHotelId());
		reservation.setRoomId(request.getRoomId());
		reservation.setGuestName(request.getGuestName());
		reservation.setGuestEmail(request.getGuestEmail());
		reservation.setCheckInDate(request.getCheckInDate());
		reservation.setCheckOutDate(request.getCheckOutDate());
		reservation.setStatus(RSTATUS.BOOKED);

		repository.save(reservation);

		return new BookingResponse(reservation.getId(), "BOOKED");
	}

	@Override
	public void cancelBooking(String reservationId) {

		Reservation reservation = repository.findById(reservationId)
				.orElseThrow(() -> new RuntimeException("Reservation not found"));

		reservation.setStatus(RSTATUS.CANCELLED);
		repository.save(reservation);
	}
}
