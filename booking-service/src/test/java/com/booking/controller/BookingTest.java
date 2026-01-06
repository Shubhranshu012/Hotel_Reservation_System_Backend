package com.booking.controller;

import com.booking.dto.BookingRequest;
import com.booking.dto.ChangeRequest;
import com.booking.dto.CheckInRequest;
import com.booking.dto.RoomResponse;
import com.booking.feign.HotelFeignClient;
import com.booking.kafka.BookingEventProducer;
import com.booking.model.RSTATUS;
import com.booking.model.Reservation;
import com.booking.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingServiceImplTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	 
	@MockBean
	private ReservationRepository reservationRepository;

	@MockBean
	private HotelFeignClient hotelFeignClient;

	@MockBean
	private BookingEventProducer bookingEventProducer;
    private Reservation existingReservation;
    private Reservation existingReservation2;
    @BeforeEach
    void setup() {
    	
        existingReservation = Reservation.builder().id("res1")
                .hotelId("hotel1").roomId("room1").guestEmail("shubhranshu.test@gmail.com").guestName("Shubhranshu Satpathy")
                .checkInDate(LocalDate.now().plusDays(5)).checkOutDate(LocalDate.now().plusDays(7)).status(RSTATUS.BOOKED).build();
        existingReservation2 = Reservation.builder().id("res2")
                .hotelId("hotel1").roomId("room1").guestEmail("shubhranshu.test2@gmail.com").guestName("Shubhranshu Satpathy")
                .checkInDate(LocalDate.now().plusDays(9)).checkOutDate(LocalDate.now().plusDays(15)).status(RSTATUS.BOOKED).build();
    }
    private BookingRequest getRequest() {
        BookingRequest request = new BookingRequest();
        request.setRoomId("room1");
        request.setGuestEmail("shubhranshu.test@gmail.com");
        request.setGuestName("Shubhranshu Satpathy");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(7));
        return request;
    }

    @Test
    void createBooking_Success() throws Exception {

        Mockito.when(reservationRepository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(anyString(), anyList(), any(), any()))
                .thenReturn(Collections.emptyList());

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("room1");
        roomResponse.setPrice(1000.0);

        Mockito.when(hotelFeignClient.getRoom(anyString(), anyString())).thenReturn(roomResponse);

        mockMvc.perform(post("/api/booking/hotel1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRequest())))
                .andExpect(status().isCreated());
    }
    @Test
    void badDateBooking() throws Exception {

    	BookingRequest request=getRequest();
    	request.setCheckOutDate(LocalDate.now().plusDays(2));
        mockMvc.perform(post("/api/booking/hotel1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_RoomAlreadyBooked() throws Exception {

        Mockito.when(reservationRepository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(anyString(), anyList(), any(), any()))
                .thenReturn(Collections.singletonList(existingReservation));

        mockMvc.perform(post("/api/booking/hotel1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test 
    void CancelBooking_Success() throws Exception{
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(delete("/api/booking/shubhranshu.test@gmail.com/res1/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRequest())))
                .andExpect(status().isOk());
        
    }

    @Test
    void cancelBooking_WrongEmail() throws Exception {
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(delete("/api/booking/wrong.test@gmail.com/res1/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRequest())))
                .andExpect(status().isNotFound());
    }
    @Test
    void getBooking_Email() throws Exception {
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(get("/api/booking/shubhranshu.test@gmail.com/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRequest())))
                .andExpect(status().isOk());
    }
    @Test
    void getBooking_Manager() throws Exception {
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(get("/api/booking/booking/hotel1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRequest())))
                .andExpect(status().isOk());
    }
    @Test
    void updateBooking() throws Exception {
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        ChangeRequest request=new ChangeRequest();
        request.setCheckInDate(LocalDate.now().plusDays(10));
        request.setCheckOutDate(LocalDate.now().plusDays(15));
        
        mockMvc.perform(put("/api/booking/shubhtanshu.test@gmai.com/res1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    @Test
    void alreadyBooked() throws Exception {
    	Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
    	Mockito.when(reservationRepository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(anyString(), anyList(), any(), any()))
        .thenReturn(Collections.singletonList(existingReservation2));
        
        ChangeRequest request=new ChangeRequest();
        request.setCheckInDate(LocalDate.now().plusDays(10));
        request.setCheckOutDate(LocalDate.now().plusDays(15));
        
        mockMvc.perform(put("/api/booking/shubhtanshu.test@gmai.com/res1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void gBookedRoomId() throws Exception {
        Mockito.when(reservationRepository.findByHotelIdAndStatusInAndCheckOutDateAfterAndCheckInDateBefore(
                anyString(), anyList(), any(), any()))
                .thenReturn(Collections.singletonList(existingReservation));

        mockMvc.perform(get("/api/booking/booked-rooms")
                .param("hotelId", "hotel1")
                .param("checkIn", LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("M/d/yy")))
                .param("checkOut", LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("M/d/yy")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void checkInBooking() throws Exception{
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(true);

        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        

        mockMvc.perform(put("/api/booking/checkin/res1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    @Test
    void badCheckInBooking() throws Exception{
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(true);
        existingReservation.setStatus(RSTATUS.CHECKED_IN);
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        

        mockMvc.perform(put("/api/booking/checkin/res1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void checkOutBooking() throws Exception{
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(false);
        existingReservation.setStatus(RSTATUS.CHECKED_IN);
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(put("/api/booking/checkin/res1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    @Test
    void badCheckOutBooking() throws Exception{
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(false);
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(put("/api/booking/checkin/res1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void badcheckOutBooking() throws Exception{
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(false);
        existingReservation.setStatus(RSTATUS.CANCELLED);
        Mockito.when(reservationRepository.findById("res1")).thenReturn(Optional.of(existingReservation));
        
        mockMvc.perform(put("/api/booking/checkin/res1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
}

