package com.classmate.auth.dto.request;

import com.classmate.auth.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Signup request")
public record SignupRequest(

		@Schema(description = "Email", example = "professor@classmate.com")
		@Email(message = "Email format is invalid.")
		@NotBlank(message = "Email is required.")
		String email,

		@Schema(description = "Password", example = "password1234")
		@NotBlank(message = "Password is required.")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
		String password,

		@Schema(description = "Name", example = "Professor Kim")
		@NotBlank(message = "Name is required.")
		@Size(max = 50, message = "Name must be at most 50 characters.")
		String name,

		@Schema(description = "User role", example = "PROFESSOR")
		@NotNull(message = "User role is required.")
		UserRole role
) {
}
