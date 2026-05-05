package com.classmate.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Question creation request")
public record CreateQuestionRequest(

		@Schema(description = "Question content", example = "Could you explain the difference between a process and a thread?")
		@NotBlank(message = "Question content is required.")
		@Size(max = 1000, message = "Question content must be at most 1000 characters.")
		String content
) {
}
