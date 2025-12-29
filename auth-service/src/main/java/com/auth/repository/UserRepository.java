package com.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.auth.model.User;


public interface UserRepository extends MongoRepository<User,String> {
	Optional<User> findByEmail(String email);
}
