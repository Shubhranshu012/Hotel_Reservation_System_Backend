package com.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ChangeRequest {
	@NotBlank(message = "Email Is Required")
	private String email;
	@NotBlank(message = "Old PassWord Is Required")
    private String oldPassword;
	@NotBlank(message = "New PassWord Is Required")
    private String newPassword;
}
