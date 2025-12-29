package com.auth.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.ChangeRequest;
import com.auth.dto.LoginRequest;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;

@RestController
public class AuthController {
	@Autowired
	UserRepository userRepository;
	@Autowired
    PasswordEncoder passwordEncoder;
	@Autowired
    JwtService jwtService;
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody User user) {
		Optional<User> users=userRepository.findByEmail(user.getEmail());
		if(!users.isEmpty()) {
			throw new RuntimeException("Email Already Exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		LocalDateTime newDate = LocalDateTime.now();
        user.setLastDate(newDate);
	    userRepository.save(user);
		return ResponseEntity.status(201).build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest request){
		Optional<User> users=userRepository.findByEmail(request.getEmail());
		if(users.isEmpty()) {
			throw new RuntimeException("Email not Found");
		}
		if (!passwordEncoder.matches(request.getPassword(), users.get().getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
		Map<String,String> responce=new HashMap<>();
		responce.put("Token",jwtService.generateToken(request.getEmail(),users.get().getRole()));
		return ResponseEntity.status(200).body(responce);
	}
	
	@PutMapping("/changePassword")
	public ResponseEntity<Map<String,String>> chnagePassword(@RequestBody ChangeRequest request){
		Optional<User> users=userRepository.findByEmail(request.getEmail());
		if(users.isEmpty()) {
			throw new RuntimeException("Email not Found");
		}
		if (!passwordEncoder.matches(request.getOldPassword(), users.get().getPassword())) {
            throw new RuntimeException("Invalid Old PassWord");
        }
		users.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
        LocalDateTime newDate = LocalDateTime.now();
        users.get().setLastDate(newDate);
        userRepository.save(users.get());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
		return ResponseEntity.status(200).body(response);
	}
}
