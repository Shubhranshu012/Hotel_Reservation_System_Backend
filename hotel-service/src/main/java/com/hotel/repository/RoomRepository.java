package com.hotel.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hotel.model.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room,String>{

}
