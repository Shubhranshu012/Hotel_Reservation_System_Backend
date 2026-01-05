package com.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.dto.ChangeRequest;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.exception.BadRequestException;
import com.auth.exception.NotFoundException;
import com.auth.model.ROLE;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;
import com.auth.util.PasswordCheck;

@Service
public class AuthServiceImpl implements AuthService{
	
	@Autowired
	UserRepository userRepository;
	@Autowired
    PasswordEncoder passwordEncoder;
	@Autowired
	JwtService jwtService;
	
	@Override
	public void register(RegisterRequest request) {
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
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    user.setRole(request.getRole());
	    userRepository.save(user);
	}
	@Override
	public Map<String, String> login(LoginRequest request) {
		Optional<User> users=userRepository.findByEmail(request.getEmail());
		if(users.isEmpty()) {
			throw new NotFoundException();
		}
		if (!passwordEncoder.matches(request.getPassword(), users.get().getPassword())) {
            throw new BadRequestException("Invalid Credentials");
        }
		Map<String,String> responce=new HashMap<>();
		responce.put("Token",jwtService.generateToken(request.getEmail(),users.get().getRole(),users.get().getHotelId()));
		responce.put("role", users.get().getRole().toString());
		responce.put("hotelId", users.get().getHotelId());
		return responce;
	}
	@Override 
	public Map<String, String> chnagePassword(ChangeRequest request) {
		if(!request.getNewPassword().equals(request.getOldPassword())) {
			throw new BadRequestException("Old Password is Not Same as New");
		}
		PasswordCheck.validate(request.getNewPassword());
		Optional<User> users=userRepository.findByEmail(request.getEmail());
		if(users.isEmpty()) {
			throw new NotFoundException();
		}
		if (!passwordEncoder.matches(request.getOldPassword(), users.get().getPassword())) {
            throw new BadRequestException("Invalid Old PassWord");
        }
		users.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(users.get());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return response;
	}
}
