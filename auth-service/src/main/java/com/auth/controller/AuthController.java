package com.auth.controller;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth.dto.ChangeRequest;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.service.AuthServiceImpl;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	AuthServiceImpl authService;
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
	    authService.register(request);
		return ResponseEntity.status(201).build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String,String>> login(@RequestBody @Valid LoginRequest request){
		Map<String,String> responce = authService.login(request);
		return ResponseEntity.status(200).body(responce);
	}
	
	@PutMapping("/changePassword")
	public ResponseEntity<Map<String,String>> chnagePassword(@RequestBody @Valid ChangeRequest request){
        Map<String, String> response = authService.chnagePassword(request);
		return ResponseEntity.status(200).body(response);
	}
	
}
