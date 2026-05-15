package com.classmate.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Chat message send request")
public record SendChatMessageRequest(

		@Schema(description = "Chat message content", example = "Could someone share the assignment link?")
		@NotBlank(message = "Chat message content is required.")
		@Size(max = 500, message = "Chat message content must be at most 500 characters.")
		String content
) {
}
