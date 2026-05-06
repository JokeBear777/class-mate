package com.classmate.realtime.dto;

import com.classmate.feedback.domain.FeedbackEvent;
import java.time.LocalDateTime;

public record RealtimeFeedbackMessage(
		RealtimeEventType eventType,
		Long sessionId,
		Long lectureId,
		Long feedbackId,
		String feedbackType,
		LocalDateTime createdAt
) {

	public static RealtimeFeedbackMessage from(FeedbackEvent feedbackEvent) {
		return new RealtimeFeedbackMessage(
				RealtimeEventType.FEEDBACK_SUBMITTED,
				feedbackEvent.getSessionId(),
				feedbackEvent.getLectureId(),
				feedbackEvent.getId(),
				feedbackEvent.getFeedbackType().name(),
				feedbackEvent.getCreatedAt()
		);
	}
}
