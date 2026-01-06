package com.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.dto.RegisterRequest;
import com.auth.exception.BadRequestException;
import com.auth.model.ROLE;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;
import com.auth.util.PasswordCheck;

@Service
public class InternalAuthImpl implements InternalAuth{
	@Autowired
	UserRepository userRepository;
	@Autowired
    PasswordEncoder passwordEncoder;
	@Autowired
	JwtService jwtService;
	
	@Override
	public void registerManager(String hotelId,RegisterRequest request) {
		if(!request.getPassword().equals(request.getConfirmPassword())) {
			throw new BadRequestException("Confirm Password is Not Same as Password");
		}
		PasswordCheck.validate(request.getPassword());
		
		Optional<User> users=userRepository.findByEmail(request.getEmail());
		if(!users.isEmpty()) {
			throw new BadRequestException("Email Already Exists");
		}
		User user = new User();
		user.setEmail(request.getEmail());
		user.setHotelId(hotelId);
		user.setRole(ROLE.MANAGER);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
	    userRepository.save(user);
	}
	@Override
	public void registerReceptionist(String hotelId,RegisterRequest request) {
		if(!request.getPassword().equals(request.getConfirmPassword())) {
			throw new BadRequestException("Confirm Password is Not Same as Password");
		}
		PasswordCheck.validate(request.getPassword());
		Optional<User> users=userRepository.findByEmail(request.getEmail());
		if(!users.isEmpty()) {
			throw new BadRequestException("Email Already Exists");
		}
		User user = new User();
		user.setHotelId(hotelId);
		user.setEmail(request.getEmail());
		user.setRole(ROLE.RECEPTIONIST);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
	    userRepository.save(user);
	}
}
