package com.booking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.RoomResponse;
import com.booking.exception.BadRequestException;
import com.booking.exception.NotFoundException;
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
	public BookingResponse createBooking(BookingRequest request,String hotelId) {

		RoomResponse room = hotelClient.getRoom(hotelId, request.getRoomId());
		List<Reservation> bookings=repository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(hotelId,List.of(RSTATUS.BOOKED,RSTATUS.CONFIRMED,RSTATUS.CHECKED_IN),request.getCheckInDate(),request.getCheckOutDate());;
		
		for (int i = 0; i < bookings.size(); i++) {
		    Reservation reservation = bookings.get(i);
		    if (reservation.getRoomId().equals(request.getRoomId())) {
		    	new BadRequestException("Room Already Booked");
		    }
		}
		
		Reservation reservation = new Reservation();
		reservation.setHotelId(hotelId);
		reservation.setRoomId(request.getRoomId());
		reservation.setGuestName(request.getGuestName());
		reservation.setGuestEmail(request.getGuestEmail());
		reservation.setCheckInDate(request.getCheckInDate());
		reservation.setCheckOutDate(request.getCheckOutDate());
		
		long diffInDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
		reservation.setStatus(RSTATUS.BOOKED);
		reservation.setPrice(room.getPrice()*diffInDays);

		repository.save(reservation);

		return new BookingResponse(reservation.getId(), "BOOKED");
	}

	@Override
	public void cancelBooking(String reservationId) {

		Reservation reservation = repository.findById(reservationId).orElseThrow(() -> new NotFoundException());
		LocalDate tomorrow = LocalDate.now().plusDays(1);

	    if (!reservation.getCheckInDate().isAfter(tomorrow)) {
	        throw new BadRequestException("Cant cancel Booking within 24Hrs");
	    }
		reservation.setStatus(RSTATUS.CANCELLED);
		repository.save(reservation);
	}

    @Override
    public List<String> getBookedRoomIds(String hotelId,LocalDate checkIn,LocalDate checkOut) {

        List<Reservation> reservations =repository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(hotelId,List.of(RSTATUS.BOOKED,RSTATUS.CONFIRMED,RSTATUS.CHECKED_IN),checkIn,checkOut);
        return reservations.stream().map(Reservation::getRoomId).distinct().collect(Collectors.toList());
    }
}
