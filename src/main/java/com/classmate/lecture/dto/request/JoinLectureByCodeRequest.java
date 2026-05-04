package com.classmate.lecture.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Lecture join request by code")
public record JoinLectureByCodeRequest(

		@Schema(description = "Lecture join code", example = "A1B2C3")
		@NotBlank(message = "Join code is required.")
		@Size(min = 4, max = 20, message = "Join code must be between 4 and 20 characters.")
		String joinCode
) {
}
