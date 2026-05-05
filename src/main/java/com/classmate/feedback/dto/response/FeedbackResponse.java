package com.classmate.feedback.dto.response;

import com.classmate.feedback.domain.FeedbackEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Feedback response")
public record FeedbackResponse(

		@Schema(description = "Feedback event ID", example = "1")
		Long feedbackId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Feedback type", example = "CONFUSED")
		String feedbackType,

		@Schema(description = "Created at")
		LocalDateTime createdAt
) {

	public static FeedbackResponse from(FeedbackEvent feedbackEvent) {
		return new FeedbackResponse(
				feedbackEvent.getId(),
				feedbackEvent.getSessionId(),
				feedbackEvent.getLectureId(),
				feedbackEvent.getFeedbackType().name(),
				feedbackEvent.getCreatedAt()
		);
	}
}
