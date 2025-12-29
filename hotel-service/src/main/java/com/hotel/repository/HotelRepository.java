package com.hotel.repository;

import org.springframework.stereotype.Repository;

import com.hotel.model.Hotels;

import org.springframework.data.mongodb.repository.MongoRepository;
@Repository
public interface HotelRepository extends MongoRepository<Hotels,String>{
	
}
