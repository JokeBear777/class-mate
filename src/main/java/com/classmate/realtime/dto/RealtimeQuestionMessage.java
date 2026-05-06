package com.classmate.realtime.dto;

import com.classmate.event.dto.QuestionCreatedEvent;
import java.time.LocalDateTime;

public record RealtimeQuestionMessage(
		RealtimeEventType eventType,
		Long sessionId,
		Long lectureId,
		Long questionId,
		String anonymousKey,
		String content,
		String status,
		boolean pinned,
		boolean hidden,
		LocalDateTime createdAt
) {

	public static RealtimeQuestionMessage from(QuestionCreatedEvent event) {
		return new RealtimeQuestionMessage(
				RealtimeEventType.QUESTION_CREATED,
				event.sessionId(),
				event.lectureId(),
				event.questionId(),
				event.anonymousKey(),
				event.content(),
				event.status(),
				event.pinned(),
				event.hidden(),
				event.createdAt()
		);
	}
}
