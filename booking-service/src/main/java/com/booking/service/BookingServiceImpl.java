package com.booking.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.feign.HotelFeignClient;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private ReservationRepository repository;

    

    @Override
    public void cancelBooking(String reservationId) {

        Reservation reservation = repository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(RSTATUS.CANCELLED);
        repository.save(reservation);
    }
}
