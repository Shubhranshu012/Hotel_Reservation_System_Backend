package com.booking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.booking.model.Reservation;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByRoomIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(
        String roomId,List<String> status,LocalDate checkIn,LocalDate checkOut);
}
