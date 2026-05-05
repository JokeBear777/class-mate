package com.classmate.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Question answer request")
public record AnswerQuestionRequest(

		@Schema(description = "Answer content", example = "A process owns resources, while a thread is an execution path inside a process.")
		@NotBlank(message = "Answer content is required.")
		@Size(max = 1000, message = "Answer content must be at most 1000 characters.")
		String answer
) {
}
