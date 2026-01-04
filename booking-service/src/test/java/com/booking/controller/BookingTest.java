package com.booking.controller;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.ChangeRequest;
import com.booking.dto.CheckInRequest;
import com.booking.feign.HotelFeignClient;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;
import com.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "eureka.client.enabled=false")
public class BookingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private HotelFeignClient hotelFeignClient;

    private Reservation reservation;

    @BeforeEach
    void setup() {
        reservation = Reservation.builder().id("res1")
                .hotelId("hotel1").roomId("room1").guestEmail("test@example.com").guestName("John Doe")
                .checkInDate(LocalDate.now().plusDays(5)).checkOutDate(LocalDate.now().plusDays(7))
                .status(RSTATUS.BOOKED).build();
    }

    @Test
    void testBookRoom_Success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setRoomId("room1");
        request.setGuestEmail("test@example.com");
        request.setGuestName("John Doe");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(7));

        Mockito.when(bookingService.createBooking(any(BookingRequest.class), eq("hotel1")))
                .thenReturn(new BookingResponse("res1", "BOOKED"));

        mockMvc.perform(post("/api/booking/hotel1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        Mockito.doNothing().when(bookingService).cancelBooking("res1", "test@example.com");

        mockMvc.perform(delete("/api/booking/test@example.com/res1/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllBookingByEmail_Success() throws Exception {
        Mockito.when(bookingService.getAllBooking("test@example.com"))
                .thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/booking/test@example.com/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsByHotel_Success() throws Exception {
        Mockito.when(reservationRepository.findByHotelId("hotel1"))
                .thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/booking/booking/hotel1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        ChangeRequest request = new ChangeRequest();
        request.setCheckInDate(LocalDate.now().plusDays(6));
        request.setCheckOutDate(LocalDate.now().plusDays(8));

        Mockito.doNothing().when(bookingService).updateBooking("res1", "test@example.com", request);

        mockMvc.perform(put("/api/booking/test@example.com/res1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCheckInBooking_Success() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(true);

        Mockito.doNothing().when(bookingService).checkInCheckOut("res1", request);

        mockMvc.perform(put("/api/booking/checkin/res1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
