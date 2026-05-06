package com.classmate.event.dto;

import com.classmate.feedback.domain.FeedbackEvent;
import java.time.LocalDateTime;

public record FeedbackSubmittedEvent(
		Long feedbackId,
		Long lectureId,
		Long sessionId,
		String feedbackType,
		LocalDateTime createdAt
) {

	public static FeedbackSubmittedEvent from(FeedbackEvent feedbackEvent) {
		return new FeedbackSubmittedEvent(
				feedbackEvent.getId(),
				feedbackEvent.getLectureId(),
				feedbackEvent.getSessionId(),
				feedbackEvent.getFeedbackType().name(),
				feedbackEvent.getCreatedAt()
		);
	}
}
