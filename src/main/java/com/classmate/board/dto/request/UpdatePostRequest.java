package com.classmate.board.dto.request;

import com.classmate.board.domain.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Post update request")
public record UpdatePostRequest(

		@Schema(description = "Post type", example = "QUESTION")
		@NotNull(message = "Post type is required.")
		PostType postType,

		@Schema(description = "Post title", example = "Updated question about assignment 2")
		@NotBlank(message = "Title is required.")
		@Size(max = 100, message = "Title must be 100 characters or fewer.")
		String title,

		@Schema(description = "Post content", example = "I updated the details of my question.")
		@NotBlank(message = "Content is required.")
		@Size(max = 3000, message = "Content must be 3000 characters or fewer.")
		String content
) {
}
