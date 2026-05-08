package com.classmate.board.dto.response;

import com.classmate.board.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Post summary response")
public record PostSummaryResponse(
		@Schema(description = "Post ID", example = "1")
		Long postId,
		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,
		@Schema(description = "Author user ID", example = "3")
		Long authorId,
		@Schema(description = "Author name", example = "Kim Minwoo")
		String authorName,
		@Schema(description = "Post type", example = "GENERAL")
		String postType,
		@Schema(description = "Post title", example = "Question about assignment 2")
		String title,
		@Schema(description = "Created time")
		LocalDateTime createdAt,
		@Schema(description = "Updated time")
		LocalDateTime updatedAt
) {

	public static PostSummaryResponse from(Post post, String authorName) {
		return new PostSummaryResponse(
				post.getId(),
				post.getLectureId(),
				post.getAuthorId(),
				authorName,
				post.getPostType().name(),
				post.getTitle(),
				post.getCreatedAt(),
				post.getUpdatedAt()
		);
	}
}
