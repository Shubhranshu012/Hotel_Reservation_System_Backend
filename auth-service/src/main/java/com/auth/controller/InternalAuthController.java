package com.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.model.ROLE;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;

@RestController
@RequestMapping("/auth")
public class InternalAuthController {
	@Autowired
	UserRepository userRepository;
	@Autowired
    PasswordEncoder passwordEncoder;
	@Autowired
	JwtService jwtService;
	
	@PostMapping("/register/manager/{hotelId}")
	public ResponseEntity<Void> registerManager(@RequestBody User user ,@PathVariable String hotelId) {
		Optional<User> users=userRepository.findByEmail(user.getEmail());
		if(!users.isEmpty()) {
			throw new RuntimeException("Email Already Exists");
		}
		user.setHotelId(hotelId);
		user.setRole(ROLE.MANAGER);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	    userRepository.save(user);
		return ResponseEntity.status(201).build();
	}
	@PostMapping("/register/receptionist/{hotelId}")
	public ResponseEntity<Void> registerReceptionist(@RequestBody User user,@PathVariable String hotelId) {
		Optional<User> users=userRepository.findByEmail(user.getEmail());
		if(!users.isEmpty()) {
			throw new RuntimeException("Email Already Exists");
		}
		user.setHotelId(hotelId);
		user.setRole(ROLE.RECEPTIONIST);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	    userRepository.save(user);
		return ResponseEntity.status(201).build();
	}
}
