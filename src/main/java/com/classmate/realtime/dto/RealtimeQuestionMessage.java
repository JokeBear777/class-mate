package com.classmate.realtime.dto;

import com.classmate.question.domain.Question;
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

	public static RealtimeQuestionMessage from(Question question) {
		return new RealtimeQuestionMessage(
				RealtimeEventType.QUESTION_CREATED,
				question.getSessionId(),
				question.getLectureId(),
				question.getId(),
				question.getAnonymousKey(),
				question.getContent(),
				question.getStatus().name(),
				question.isPinned(),
				question.isHidden(),
				question.getCreatedAt()
		);
	}
}
