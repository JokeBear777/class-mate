package com.classmate.event.dto;

import com.classmate.question.domain.Question;
import java.time.LocalDateTime;

public record QuestionCreatedEvent(
		Long questionId,
		Long lectureId,
		Long sessionId,
		String anonymousKey,
		String content,
		String status,
		boolean pinned,
		boolean hidden,
		LocalDateTime createdAt
) {

	public static QuestionCreatedEvent from(Question question) {
		return new QuestionCreatedEvent(
				question.getId(),
				question.getLectureId(),
				question.getSessionId(),
				question.getAnonymousKey(),
				question.getContent(),
				question.getStatus().name(),
				question.isPinned(),
				question.isHidden(),
				question.getCreatedAt()
		);
	}
}
