package com.hotel.feign;


import org.springframework.stereotype.Component;
import com.hotel.dto.CheckInRequest;
import com.hotel.exception.ServiceUnavailableException;
import java.time.LocalDate;
import java.util.List;

@Component
public class BookingServiceFallback implements BookingFeignClient {

    @Override
    public List<String> getBookedRooms(String hotelId, LocalDate checkIn, LocalDate checkOut) {
        throw new ServiceUnavailableException("Booking service is currently unavailable. Please try again later.");
    }

    @Override
    public void checkIn(String bookingId, CheckInRequest request) {
        throw new ServiceUnavailableException("Booking service is currently unavailable. Please try again later.");
    }
}

