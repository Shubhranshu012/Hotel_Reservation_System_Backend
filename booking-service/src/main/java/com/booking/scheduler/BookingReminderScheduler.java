package com.booking.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.booking.dto.BookingEvent;
import com.booking.kafka.BookingEventProducer;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingReminderScheduler {
	
	@Autowired
    ReservationRepository reservationRepository;
	
	@Autowired
    BookingEventProducer bookingEventProducer;

    
	@Scheduled(cron = "0 10 22 * * *", zone = "Asia/Kolkata")
    public void sendCheckInReminders() {

        LocalDate nextDay = LocalDate.now().plusDays(1);

        List<Reservation> bookings =reservationRepository.findByStatusAndCheckInDateAndCheckInReminderSentFalse(RSTATUS.BOOKED,nextDay);

        for (Reservation reservation : bookings) {
            BookingEvent event = new BookingEvent();
            event.setEventType("CHECK_IN_REMINDER");
            event.setReservationId(reservation.getId());
            event.setHotelId(reservation.getHotelName());
            event.setRoomId(reservation.getRoomId());
            event.setGuestEmail(reservation.getGuestEmail());
            event.setCheckIn(reservation.getCheckInDate().toString());
            event.setCheckOut(reservation.getCheckOutDate().toString());

            bookingEventProducer.sendEvent(event);

            reservation.setCheckInReminderSent(true);
        }

        reservationRepository.saveAll(bookings);
    }

 
	@Scheduled(cron = "0 10 22  * * *", zone = "Asia/Kolkata")
    public void sendCheckOutReminders() {

        LocalDate nextDay = LocalDate.now().plusDays(1);

        List<Reservation> bookings =reservationRepository.findByStatusAndCheckOutDateAndCheckOutReminderSentFalse(RSTATUS.BOOKED,nextDay);

        for (Reservation reservation : bookings) {
            BookingEvent event = new BookingEvent();
            event.setEventType("CHECK_OUT_REMINDER");
            event.setReservationId(reservation.getId());
            event.setHotelId(reservation.getHotelName());
            event.setRoomId(reservation.getRoomId());
            event.setGuestEmail(reservation.getGuestEmail());
            event.setCheckIn(reservation.getCheckInDate().toString());
            event.setCheckOut(reservation.getCheckOutDate().toString());

            bookingEventProducer.sendEvent(event);

            reservation.setCheckOutReminderSent(true);
        }

        reservationRepository.saveAll(bookings);
    }
}
