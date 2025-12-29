package com.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class LoginRequest {
	@NotBlank(message = "Email Is Required")
	private String email;
	@NotBlank(message = "PassWord Is Required")
    private String password;
}
