package com.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.*;
import com.hotel.exception.NotFoundException;
import com.hotel.feign.BookingFeignClient;
import com.hotel.model.*;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "eureka.client.enabled=false")
public class HotelTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelRepository hotelRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private BookingFeignClient bookingFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Hotels hotel;
    private Room room;

    @BeforeEach
    void setup() {
        hotel = Hotels.builder().id("hotel1").hotelName("Test Hotel")
                .city("Test City").address("Test Address").numberOfRooms(10).status(HSTATUS.ACTIVE).booked(0).build();

        room = Room.builder().id("room1").hotelId("hotel1")
                .roomNumber("101").type(RTYPE.DELUXE).status(RSTATUS.AVAILABLE).price(100.0).build();

        
        Mockito.when(hotelRepository.findByHotelNameAndCityAndAddress(anyString(), anyString(), anyString())).thenReturn(null);
        Mockito.when(hotelRepository.save(any(Hotels.class))).thenReturn(hotel);
        Mockito.when(hotelRepository.findByIdAndStatus(anyString(), eq(HSTATUS.ACTIVE))).thenReturn(java.util.Optional.of(hotel));
        Mockito.when(hotelRepository.findAll()).thenReturn(Arrays.asList(hotel));
        Mockito.when(hotelRepository.findByCityAndStatus(anyString(), eq(HSTATUS.ACTIVE))).thenReturn(Arrays.asList(hotel));
        Mockito.when(roomRepository.findByHotelId(anyString())).thenReturn(Arrays.asList(room));
        Mockito.when(roomRepository.save(any(Room.class))).thenReturn(room);
        Mockito.when(roomRepository.findByIdAndHotelId(anyString(), anyString())).thenReturn(java.util.Optional.of(room));
        Mockito.when(bookingFeignClient.getBookedRooms(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());
    }
    

    @Test
    void testAddHotel() throws Exception {
        InventoryRequest request = new InventoryRequest();
        request.setHotelName("New Hotel");
        request.setCity("New City");
        request.setAddress("New Address");
        request.setNumberOfRooms(5);

        mockMvc.perform(post("/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateHotel() throws Exception {
        UpdateHotelRequest request = new UpdateHotelRequest();
        request.setHotelName("Updated Hotel");

        mockMvc.perform(put("/hotel/hotel1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteHotel() throws Exception {
        mockMvc.perform(delete("/hotel/hotel1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllHotels() throws Exception {
        mockMvc.perform(get("/hotel/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllHotels_EmptyList() throws Exception {
        Mockito.when(hotelRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(bookingFeignClient.getBookedRooms(anyString(), any(LocalDate.class), any(LocalDate.class))).thenThrow(new NotFoundException());

        mockMvc.perform(get("/hotel/all"))
                .andExpect(status().isNotFound());
    }

    

    @Test
    void testAddRooms() throws Exception {
        RoomRequest newRoom = new RoomRequest();
        newRoom.setPrice(5120.0);
        newRoom.setRoomNumber("1011");
        newRoom.setStatus(RSTATUS.AVAILABLE);
        newRoom.setType(RTYPE.SUITE);

        List<RoomRequest> requests = Arrays.asList(newRoom);

        mockMvc.perform(post("/hotel/hotel1/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateRooms() throws Exception {
        UpdateRoomRequest request = new UpdateRoomRequest();
        request.setPrice(4000.0);
        request.setStatus(RSTATUS.MAINTENANCE);
        request.setType(RTYPE.SINGLE);

        mockMvc.perform(put("/hotel/hotel1/room/room1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoom() throws Exception {
        mockMvc.perform(get("/hotel/hotel1/room/room1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoom_NotFound() throws Exception {
        Mockito.when(roomRepository.findByIdAndHotelId(anyString(), anyString())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/hotel/hotel134/room/INVALID"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testSearchHotels() throws Exception {
        HotelSearchRequest request = new HotelSearchRequest();
        request.setCity("Test City");
        request.setCheckIn(LocalDate.now());
        request.setCheckOut(LocalDate.now().plusDays(1));
        request.setRoomCount(1);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAvailableRooms() throws Exception {
        RoomAvailabilityRequest request = new RoomAvailabilityRequest();
        request.setCheckIn(LocalDate.now());
        request.setCheckOut(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/hotel/hotel1/rooms/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAvailableRooms_Empty() throws Exception {

        RoomAvailabilityRequest request = new RoomAvailabilityRequest();
        request.setCheckIn(LocalDate.now());
        request.setCheckOut(LocalDate.now().plusDays(1));

        Mockito.when(roomRepository.findByHotelId(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/hotel/hotel14/rooms/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
