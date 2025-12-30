package com.booking.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.booking.dto.RoomResponse;

@FeignClient(name = "hotel-service", url = "http://localhost:8001")
public interface HotelFeignClient {

	@GetMapping("/api/hotel/{hotelId}/room/{roomId}")
    RoomResponse getRoom(
        @PathVariable String hotelId,
        @PathVariable String roomId
    );
}