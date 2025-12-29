package com.hotel.repository;

import org.springframework.stereotype.Repository;

import com.hotel.model.HSTATUS;
import com.hotel.model.Hotels;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
@Repository
public interface HotelRepository extends MongoRepository<Hotels,String>{
	Optional<Hotels> findByIdAndStatus(String id, HSTATUS status);
	Optional<Hotels> findById(String id);
	Hotels findByHotelNameAndCityAndAddress(String hotelName,String city,String address);
}
