package com.classmate.auth.dto.response;

import com.classmate.auth.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response")
public record AuthResponse(

		@Schema(description = "JWT access token")
		String accessToken,

		@Schema(description = "Token type", example = "Bearer")
		String tokenType,

		@Schema(description = "User ID", example = "1")
		Long userId,

		@Schema(description = "Email", example = "professor@classmate.com")
		String email,

		@Schema(description = "Name", example = "Professor Kim")
		String name,

		@Schema(description = "Role", example = "PROFESSOR")
		String role
) {

	public static AuthResponse from(String accessToken, User user) {
		return new AuthResponse(
				accessToken,
				"Bearer",
				user.getId(),
				user.getEmail(),
				user.getName(),
				user.getRole().name()
		);
	}
}
