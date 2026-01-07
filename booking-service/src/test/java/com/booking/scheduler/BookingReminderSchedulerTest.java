package com.booking.scheduler;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.booking.dto.BookingEvent;
import com.booking.kafka.BookingEventProducer;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class BookingReminderSchedulerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookingEventProducer bookingEventProducer;

    @InjectMocks
    private BookingReminderScheduler bookingReminderScheduler;

    private Reservation reservation1;
    private Reservation reservation2;
    private LocalDate nextDay;

    @BeforeEach
    void setUp() {
        nextDay = LocalDate.now().plusDays(1);

        reservation1 = new Reservation();
        reservation1.setId("1");
        reservation1.setHotelName("Hotel A");
        reservation1.setRoomId("101");
        reservation1.setGuestEmail("guest1@example.com");
        reservation1.setCheckInDate(nextDay);
        reservation1.setCheckOutDate(nextDay.plusDays(2));
        reservation1.setCheckInReminderSent(false);

        reservation2 = new Reservation();
        reservation2.setId("2");
        reservation2.setHotelName("Hotel B");
        reservation2.setRoomId("102");
        reservation2.setGuestEmail("guest2@example.com");
        reservation2.setCheckInDate(nextDay);
        reservation2.setCheckOutDate(nextDay.plusDays(3));
        reservation2.setCheckOutReminderSent(false);
    }

    @Test
    void sendCheckInReminders() {
        List<Reservation> reservations = Arrays.asList(reservation1);
        when(reservationRepository.findByStatusAndCheckInDateAndCheckInReminderSentFalse(RSTATUS.BOOKED, nextDay)).thenReturn(reservations);
        bookingReminderScheduler.sendCheckInReminders();

        verify(bookingEventProducer, times(1)).sendEvent(any(BookingEvent.class));
        verify(reservationRepository, times(1)).saveAll(reservations);
        assert reservation1.isCheckInReminderSent();
    }

    @Test
    void sendCheckOutReminders() {
        List<Reservation> reservations = Arrays.asList(reservation2);
        when(reservationRepository.findByStatusAndCheckOutDateAndCheckOutReminderSentFalse(RSTATUS.BOOKED, nextDay)).thenReturn(reservations);

        bookingReminderScheduler.sendCheckOutReminders();

        verify(bookingEventProducer, times(1)).sendEvent(any(BookingEvent.class));
        verify(reservationRepository, times(1)).saveAll(reservations);
        assert reservation2.isCheckOutReminderSent();
    }

    @Test
    void badSendCheckInReminders() {
        when(reservationRepository.findByStatusAndCheckInDateAndCheckInReminderSentFalse(RSTATUS.BOOKED, nextDay)).thenReturn(Arrays.asList());

        bookingReminderScheduler.sendCheckInReminders();

        verify(bookingEventProducer, never()).sendEvent(any(BookingEvent.class));
        verify(reservationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void badSendCheckOutReminders() {
        when(reservationRepository.findByStatusAndCheckOutDateAndCheckOutReminderSentFalse(RSTATUS.BOOKED, nextDay)).thenReturn(Arrays.asList());

        bookingReminderScheduler.sendCheckOutReminders();

        verify(bookingEventProducer, never()).sendEvent(any(BookingEvent.class));
        verify(reservationRepository, times(1)).saveAll(anyList());
    }
}