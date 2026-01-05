package com.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.RegisterRequest;
import com.auth.service.InternalAuthImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class InternalAuthController {
	@Autowired
	InternalAuthImpl internalService;
	
	@PostMapping("/register/manager/{hotelId}")
	public ResponseEntity<Void> registerManager(@RequestBody @Valid RegisterRequest request ,@PathVariable String hotelId) {
		internalService.registerManager(hotelId, request);
		return ResponseEntity.status(201).build();
	}
	@PostMapping("/register/receptionist/{hotelId}")
	public ResponseEntity<Void> registerReceptionist(@RequestBody @Valid RegisterRequest request,@PathVariable String hotelId) {
		internalService.registerReceptionist(hotelId, request);
		return ResponseEntity.status(201).build();
	}
}
