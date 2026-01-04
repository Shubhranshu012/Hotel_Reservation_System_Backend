package com.booking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;

import com.booking.dto.BookingEvent;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.ChangeRequest;
import com.booking.dto.CheckInRequest;
import com.booking.dto.RoomResponse;
import com.booking.exception.BadRequestException;
import com.booking.exception.NotFoundException;
import com.booking.feign.HotelFeignClient;
import com.booking.kafka.BookingEventProducer;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private ReservationRepository repository;

	@Autowired
	private HotelFeignClient hotelClient;
	
	@Autowired
	private BookingEventProducer bookingEventProducer;

	@Override
	public BookingResponse createBooking(BookingRequest request,String hotelId) {

		RoomResponse room = hotelClient.getRoom(hotelId, request.getRoomId());
		List<Reservation> bookings=repository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(hotelId,List.of(RSTATUS.BOOKED,RSTATUS.CONFIRMED,RSTATUS.CHECKED_IN),request.getCheckInDate(),request.getCheckOutDate());;
		
		for (int i = 0; i < bookings.size(); i++) {
		    Reservation reservation = bookings.get(i);
		    if (reservation.getRoomId().equals(request.getRoomId())) {
		    	throw new BadRequestException("Room Already Booked");
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
		BookingEvent event = new BookingEvent();
		event.setEventType("BOOKING_CONFIRMED");
		event.setReservationId(reservation.getId());
		event.setHotelId(reservation.getHotelId());
		event.setRoomId(reservation.getRoomId());
		event.setGuestEmail(reservation.getGuestEmail());
		event.setCheckIn(reservation.getCheckInDate().toString());
		event.setCheckOut(reservation.getCheckOutDate().toString());

		bookingEventProducer.sendEvent(event);
		return new BookingResponse(reservation.getId(), "BOOKED");
	}

	@Override
	public void cancelBooking(String reservationId,String email) {

		Reservation reservation = repository.findById(reservationId).orElseThrow(() -> new NotFoundException());
		if(!reservation.getGuestEmail().equals(email)) {
			throw new NotFoundException();
		}
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
    @Override
    public List<Reservation> getAllBooking(String email){
    	return repository.findByGuestEmail(email);
    }
    
    @Override
    public void updateBooking(String reservationId, String email, ChangeRequest request) {

        Reservation existingReservation = repository.findById(reservationId).orElseThrow(NotFoundException::new);

        List<Reservation> bookings =repository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(existingReservation.getHotelId(),List.of(RSTATUS.BOOKED, RSTATUS.CONFIRMED, RSTATUS.CHECKED_IN),request.getCheckInDate(),request.getCheckOutDate());
        for (Reservation booking : bookings) {
            if (!booking.getId().equals(reservationId) && booking.getRoomId().equals(existingReservation.getRoomId())) {
                throw new BadRequestException("Room Already Booked");
            }
        }

        existingReservation.setCheckInDate(request.getCheckInDate());
        existingReservation.setCheckOutDate(request.getCheckOutDate());

        repository.save(existingReservation);
    }
    @Override
    public void checkInCheckOut(String reservationId,CheckInRequest checkInRequest) {
    	Reservation existingReservation = repository.findById(reservationId).orElseThrow(NotFoundException::new);
    	if(existingReservation.getStatus() == RSTATUS.BOOKED) {
    		if(checkInRequest.getCheckIn()) {
    			existingReservation.setStatus(RSTATUS.CHECKED_IN);
    			repository.save(existingReservation);
    		}
    		else {
    			throw new BadRequestException("Status is Not CheckIn");
    		}
    	}
    	else if(existingReservation.getStatus() == RSTATUS.CHECKED_IN){
    		if(checkInRequest.getCheckIn()) {
    			throw new BadRequestException("Booking is Already CheckIn");
    		}
    		else {
    			existingReservation.setStatus(RSTATUS.CHECKED_OUT);
    			repository.save(existingReservation);
    		}
    	}
    	else {
    		throw new BadRequestException("Booking Status Is not Booked OR CheckedIn");
    	}
    }

}
