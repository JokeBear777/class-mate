package com.classmate.question.dto.response;

import com.classmate.question.domain.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Question response")
public record QuestionResponse(

		@Schema(description = "Question ID", example = "1")
		Long questionId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Anonymous author key", example = "anon-12")
		String anonymousKey,

		@Schema(description = "Question content", example = "Could you explain the difference between a process and a thread?")
		String content,

		@Schema(description = "Answer content", example = "A process owns resources, while a thread is an execution path inside a process.")
		String answer,

		@Schema(description = "Question status", example = "WAITING")
		String status,

		@Schema(description = "Whether the question is pinned", example = "false")
		boolean pinned,

		@Schema(description = "Whether the question is hidden", example = "false")
		boolean hidden,

		@Schema(description = "Created at")
		LocalDateTime createdAt,

		@Schema(description = "Answered at")
		LocalDateTime answeredAt
) {

	public static QuestionResponse from(Question question) {
		return new QuestionResponse(
				question.getId(),
				question.getSessionId(),
				question.getLectureId(),
				question.getAnonymousKey(),
				question.getContent(),
				question.getAnswer(),
				question.getStatus().name(),
				question.isPinned(),
				question.isHidden(),
				question.getCreatedAt(),
				question.getAnsweredAt()
		);
	}
}
