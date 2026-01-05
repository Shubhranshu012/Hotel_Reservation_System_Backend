package com.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.CheckInRequest;
import com.hotel.feign.BookingFeignClient;
import com.hotel.model.RTYPE;
import com.hotel.model.RSTATUS;
import com.hotel.model.Room;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Arrays;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoomRepository roomRepository;
	@MockBean
	private HotelRepository hotelRepository;

	@MockBean
	private BookingFeignClient bookingFeignClient;
	
	
	@Autowired
	private ObjectMapper objectMapper;

	private Room room;
	private Room occupiedRoom;

	@BeforeEach
	void setup() {
	    room = Room.builder().id("room1").hotelId("hotel1").roomNumber("101").type(RTYPE.DELUXE).status(RSTATUS.AVAILABLE).price(100.0).build();
	    occupiedRoom = Room.builder().id("roomOccupied").hotelId("hotel1").roomNumber("102").type(RTYPE.DELUXE).status(RSTATUS.OCCUPIED).price(100.0).build();

	    Mockito.when(roomRepository.findByHotelId("hotel1")).thenReturn(Arrays.asList(room));
	    Mockito.when(roomRepository.findByIdAndHotelId("room1", "hotel1")).thenReturn(java.util.Optional.of(room));
	    Mockito.when(roomRepository.findByIdAndHotelId("roomOccupied", "hotel1")).thenReturn(java.util.Optional.of(occupiedRoom));
	    Mockito.when(roomRepository.save(any(Room.class))).thenReturn(room);
	    Mockito.when(hotelRepository.findByIdAndStatus(anyString(), any())).thenReturn(java.util.Optional.of(com.hotel.model.Hotels.builder().id("hotel1").status(com.hotel.model.HSTATUS.ACTIVE).build()));
	}
	
	CheckInRequest getCheckIn() {
		CheckInRequest request = new CheckInRequest();
	    request.setCheckIn(true);
	    return request;
	}
	
	@Test
	void getAllRooms() throws Exception {
	    mockMvc.perform(get("/rooms/hotel1"))
	            .andExpect(status().isOk());
	}

	@Test
	void badGetAllRooms() throws Exception {
	    Mockito.when(roomRepository.findByHotelId("emptyHotel")).thenReturn(Collections.emptyList());

	    mockMvc.perform(get("/rooms/emptyHotel"))
	            .andExpect(status().isOk());
	}
	@Test
	void getRoomsByHotel() throws Exception {
	    mockMvc.perform(get("/rooms/hotel1"))
	            .andExpect(status().isOk());
	}
	@Test
	void deleteRoom() throws Exception {
	    Mockito.doNothing().when(roomRepository).delete(any(Room.class));

	    mockMvc.perform(MockMvcRequestBuilders.delete("/hotel/hotel1/room/room1"))
	    		.andExpect(status().isOk());
	}
	@Test
	void badDeleteRoom() throws Exception {
	    Mockito.when(roomRepository.findByIdAndHotelId("roomOccupied", "hotel1")).thenReturn(java.util.Optional.of(occupiedRoom));

	    mockMvc.perform(MockMvcRequestBuilders.delete("/hotel/hotel1/room/roomOccupied"))
	    		.andExpect(status().isBadRequest());
	}

	@Test
	void checkInRoom() throws Exception {
		
	    mockMvc.perform(put("/hotel1/rooms/room1/booking1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(getCheckIn())))
	            .andExpect(status().isOk());
	}

	@Test
	void checkOutRoom() throws Exception {
	    CheckInRequest request = getCheckIn();
	    request.setCheckIn(false);

	    Room occupiedRoom1= Room.builder().id("room1").hotelId("hotel1").roomNumber("101").type(RTYPE.DELUXE).status(RSTATUS.OCCUPIED).price(100.0).build();
	    Mockito.when(roomRepository.findByIdAndHotelId("room1", "hotel1")).thenReturn(java.util.Optional.of(occupiedRoom1));

	    mockMvc.perform(put("/hotel1/rooms/room1/booking1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isOk());
	}

	@Test
	void checkInRoom_AlreadyOccupied() throws Exception {

	    mockMvc.perform(put("/hotel1/rooms/roomOccupied/booking1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(getCheckIn())))
	            .andExpect(status().isBadRequest());
	}
	@Test
	void failCheckInRoom() throws Exception {
		
	    mockMvc.perform(put("/hotel/rooms/invalid/booking")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(getCheckIn())))
	            .andExpect(status().isNotFound());
	}
}
