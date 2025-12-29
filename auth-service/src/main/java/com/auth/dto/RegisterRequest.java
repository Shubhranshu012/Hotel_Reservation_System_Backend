package com.auth.dto;

import com.auth.model.ROLE;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class RegisterRequest {
	@NotBlank(message = "Email Is Required")
	private String email;
	@NotBlank(message = "PassWord Is Required")
    private String password;
    private ROLE role;
}
