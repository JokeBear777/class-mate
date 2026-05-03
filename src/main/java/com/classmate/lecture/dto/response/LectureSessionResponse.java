package com.classmate.lecture.dto.response;

import com.classmate.lecture.domain.LectureSession;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Lecture session response")
public record LectureSessionResponse(

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Session title", example = "Week 3 - Processes and Threads")
		String title,

		@Schema(description = "Session status", example = "ACTIVE")
		String status,

		@Schema(description = "Started at")
		LocalDateTime startedAt,

		@Schema(description = "Ended at")
		LocalDateTime endedAt
) {

	public static LectureSessionResponse from(LectureSession session) {
		return new LectureSessionResponse(
				session.getId(),
				session.getLectureId(),
				session.getTitle(),
				session.getStatus().name(),
				session.getStartedAt(),
				session.getEndedAt()
		);
	}
}
