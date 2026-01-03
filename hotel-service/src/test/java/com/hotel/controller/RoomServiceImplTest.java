package com.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.CheckInRequest;
import com.hotel.feign.BookingFeignClient;
import com.hotel.model.RTYPE;
import com.hotel.model.RSTATUS;
import com.hotel.model.Room;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.HotelServiceImpl;
import com.hotel.service.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@TestPropertySource(properties = "eureka.client.enabled=false")
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomServiceImpl roomService;

    @MockBean
    private HotelServiceImpl hotelService;
    
    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private HotelRepository hotelRepository;

    @MockBean
    private BookingFeignClient bookingFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Room room;

    @BeforeEach
    void setup() {
        room = Room.builder().id("room1").hotelId("hotel1").roomNumber("101").type(RTYPE.DELUXE).status(RSTATUS.AVAILABLE).price(100.0).build();

        Mockito.when(roomService.getRoomsByHotel("hotel1")).thenReturn(Arrays.asList(room));
        Mockito.doNothing().when(roomService).CheckInCheckOut(eq("hotel1"), eq("room1"), any(CheckInRequest.class));
        Mockito.doThrow(new com.hotel.exception.BadRequestException("Room Already Checked In"))
                .when(roomService).CheckInCheckOut(eq("hotel1"), eq("roomOccupied"), any(CheckInRequest.class));
    }

    @Test
    void testGetAllRooms_Success() throws Exception {
        mockMvc.perform(get("/rooms/hotel1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllRooms_Empty() throws Exception {
        Mockito.when(roomService.getRoomsByHotel("emptyHotel")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/rooms/emptyHotel"))
                .andExpect(status().isOk());
    }

    @Test
    void testCheckInRoom_Success() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(true);

        mockMvc.perform(put("/hotel1/rooms/room1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                		.andExpect(status().isOk());
    }

    @Test
    void testCheckOutRoom_Success() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(false);

        mockMvc.perform(put("/hotel1/rooms/room1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                		.andExpect(status().isOk());
    }

    @Test
    void testCheckInRoom_AlreadyOccupied() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setCheckIn(true);

        mockMvc.perform(put("/hotel1/rooms/roomOccupied")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                		.andExpect(status().isBadRequest());
    }
}
