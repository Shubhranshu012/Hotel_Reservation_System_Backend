package com.booking.controller;

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
import com.booking.service.BookingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelFeignClient hotelFeignClient;

    @Mock
    private BookingEventProducer bookingEventProducer;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Reservation existingReservation;

    @BeforeEach
    void setup() {
    	
        existingReservation = Reservation.builder().id("res1")
                .hotelId("hotel1").roomId("room1").guestEmail("shubhranshu.test@gmail.com").guestName("Shubhranshu Satpathy")
                .checkInDate(LocalDate.now().plusDays(5)).checkOutDate(LocalDate.now().plusDays(7)).status(RSTATUS.BOOKED).build();
    }

    @Test
    void testCreateBooking_Success() {
        Mockito.when(reservationRepository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(anyString(), anyList(), any(), any()))
                .thenReturn(Collections.emptyList());

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("room1");
        roomResponse.setPrice(1000.0);
        Mockito.when(hotelFeignClient.getRoom(anyString(), anyString()))
                .thenReturn(roomResponse);

        BookingRequest request = new BookingRequest();
        request.setRoomId("room1");
        request.setGuestEmail("shubhranshu.test@gmail.com");
        request.setGuestName("Shubhranshu Satpathy");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(7));

        BookingResponse response = bookingService.createBooking(request, "hotel1");

        assertNotNull(response);
        assertEquals("BOOKED", response.getStatus());
        Mockito.verify(reservationRepository, Mockito.times(1)).save(any(Reservation.class));
    }

}

