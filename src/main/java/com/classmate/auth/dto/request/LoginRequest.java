package com.classmate.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request")
public record LoginRequest(

		@Schema(description = "Email", example = "professor@classmate.com")
		@Email(message = "Email format is invalid.")
		@NotBlank(message = "Email is required.")
		String email,

		@Schema(description = "Password", example = "password1234")
		@NotBlank(message = "Password is required.")
		String password
) {
}
