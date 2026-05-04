package com.classmate.auth.dto.response;

import com.classmate.auth.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current user response")
public record UserResponse(

		@Schema(description = "User ID", example = "1")
		Long userId,

		@Schema(description = "Email", example = "professor@classmate.com")
		String email,

		@Schema(description = "Name", example = "Professor Kim")
		String name,

		@Schema(description = "Role", example = "PROFESSOR")
		String role
) {

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().name());
	}
}
