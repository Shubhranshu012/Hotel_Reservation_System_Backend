package com.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	
	@PostMapping("/register")
	public ResponseEntity<Void> register() {
		return ResponseEntity.status(201).build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String,String>> login(){
		Map<String,String> responce=new HashMap<>();
		return ResponseEntity.status(200).body(responce);
	}
}
