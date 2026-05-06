package com.classmate.realtime.dto;

import com.classmate.event.dto.FeedbackSubmittedEvent;
import java.time.LocalDateTime;

public record RealtimeFeedbackMessage(
		RealtimeEventType eventType,
		Long sessionId,
		Long lectureId,
		Long feedbackId,
		String feedbackType,
		LocalDateTime createdAt
) {

	public static RealtimeFeedbackMessage from(FeedbackSubmittedEvent event) {
		return new RealtimeFeedbackMessage(
				RealtimeEventType.FEEDBACK_SUBMITTED,
				event.sessionId(),
				event.lectureId(),
				event.feedbackId(),
				event.feedbackType(),
				event.createdAt()
		);
	}
}
