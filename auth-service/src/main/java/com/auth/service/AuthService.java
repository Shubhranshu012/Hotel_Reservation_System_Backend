package com.auth.service;

import java.util.Map;

import com.auth.dto.ChangeRequest;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;

public interface AuthService {
	public void register(RegisterRequest request);
	public Map<String, String> login(LoginRequest request);
	public Map<String, String> chnagePassword(ChangeRequest request) ;
}
