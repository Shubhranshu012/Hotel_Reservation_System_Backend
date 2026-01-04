package com.booking.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.booking.dto.BookingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
@SpringBootTest
@AutoConfigureMockMvc
class InputTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
	
	BookingRequest getBooking() {
		BookingRequest bookingRequest=new BookingRequest();
		bookingRequest.setCheckInDate(LocalDate.now().plusDays(2));
		bookingRequest.setCheckInDate(LocalDate.now().plusDays(4));
		bookingRequest.setGuestName("Shubhranshu Satpathy");
		bookingRequest.setGuestEmail("shubhranshu.Test@gmail.com");
		bookingRequest.setRoomId("Room1");
		return bookingRequest;
    }
	@Test
	void badBookingEmail() throws Exception {
	    BookingRequest request = getBooking();
	    request.setGuestEmail("");

	    mockMvc.perform(post("/api/booking/hotel1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());
	}
	@Test
	void badBookingName() throws Exception {
	    BookingRequest request = getBooking();
	    request.setGuestEmail("");

	    mockMvc.perform(post("/api/booking/hotel1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());
	}
	@Test
	void badBookingCheckOutDate() throws Exception {
	    BookingRequest request = getBooking();
	    request.setCheckOutDate(LocalDate.now().minusDays(2));

	    mockMvc.perform(post("/api/booking/hotel1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());
	}
	@Test
	void badBookingCheckInDate() throws Exception {
	    BookingRequest request = getBooking();
	    request.setCheckInDate(LocalDate.now().minusDays(2));

	    mockMvc.perform(post("/api/booking/hotel1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());
	}
	@Test
	void badBookingRoomId() throws Exception {
	    BookingRequest request = getBooking();
	    request.setRoomId("");

	    mockMvc.perform(post("/api/booking/hotel1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());
	}
	
}
