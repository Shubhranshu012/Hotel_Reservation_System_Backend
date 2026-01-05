package com.auth.service;

import com.auth.dto.RegisterRequest;

public interface InternalAuth {
	public void registerManager(String hotelId,RegisterRequest request);
	public void registerReceptionist(String hotelId,RegisterRequest request);
}
