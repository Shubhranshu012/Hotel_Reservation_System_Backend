package com.hotel.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.InventoryRequest;
import com.hotel.dto.UpdateHotelRequest;
@SpringBootTest
@AutoConfigureMockMvc
public class InputTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
	
	@Test
    void badTestUpdateHotel() throws Exception {
        UpdateHotelRequest request = new UpdateHotelRequest();
        request.setHotelName("Updated Hotel");
        request.setNumberOfRooms(-10);

        mockMvc.perform(put("/hotel/hotel1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
	@Test
    void badTestAddHotel() throws Exception {
		InventoryRequest request = new InventoryRequest();
        request.setHotelName("");

        mockMvc.perform(post("/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
	@Test
    void badRoomCountAddHotel() throws Exception {
		InventoryRequest request = new InventoryRequest();
		request.setHotelName("New Hotel");
        request.setCity("New City");
        request.setAddress("New Address");
        request.setNumberOfRooms(-15);

        mockMvc.perform(post("/hotel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
	
}
