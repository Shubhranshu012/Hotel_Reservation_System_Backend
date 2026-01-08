package com.booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;

@SpringBootTest
class BookingConcurrencyTest {

    @Autowired
    private ReservationRepository repository;

    @Test
    void testMultiple() throws Exception {

        Reservation reservation = Reservation.builder().hotelId("hotel1").roomId("room1").guestEmail("shubhranshu.satpathy@gmail.com")
                .checkInDate(LocalDate.now().plusDays(2)).checkOutDate(LocalDate.now().plusDays(4))
                .status(RSTATUS.BOOKED).build();

        Reservation saved = repository.save(reservation);
        final String reservationId = saved.getId();
        AtomicInteger count = new AtomicInteger();

        Runnable task = () -> {
            try {
                Reservation r = repository.findById(reservationId).orElseThrow();
                r.setStatus(RSTATUS.CANCELLED);
                repository.save(r);
            } catch (Exception e) {
                count.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);
        Thread t4 = new Thread(task);
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        Reservation finalRes = repository.findById(reservationId).orElseThrow();
        assertEquals(1L, finalRes.getVersion());
        assertEquals(3, count.get());
    }
}