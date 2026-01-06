package com.booking.feign;

import org.springframework.stereotype.Component;

import com.booking.dto.RoomResponse;
import com.booking.exception.ServiceUnavailableException;

@Component
public class HotelFeignClientFallback implements HotelFeignClient {

    @Override
    public RoomResponse getRoom(String hotelId, String roomId) {
        throw new ServiceUnavailableException("Hotel service is unavailable");
    }
}
