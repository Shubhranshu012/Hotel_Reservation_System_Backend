package com.auth.dto;

import com.auth.model.ROLE;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class RegisterRequest {
	@NotBlank(message = "Email Is Required")
	@Email(message = "Invalid email format")
	private String email;
	@NotBlank(message = "PassWord Is Required")
    private String password;
	@NotBlank(message = "Confirm PassWord Is Required")
    private String confirmPassword;
    private ROLE role;
}
