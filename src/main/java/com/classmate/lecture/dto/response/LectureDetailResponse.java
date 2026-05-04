package com.classmate.lecture.dto.response;

import com.classmate.lecture.domain.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Lecture detail response")
public record LectureDetailResponse(

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Lecture title", example = "Operating Systems")
		String title,

		@Schema(description = "Professor name", example = "Professor Kim")
		String professorName,

		@Schema(description = "Semester", example = "2026-1")
		String semester,

		@Schema(description = "Lecture description")
		String description,

		@Schema(description = "Whether the current user is enrolled", example = "true")
		boolean enrolled,

		@Schema(description = "Current user's role in the lecture", example = "PROFESSOR")
		String role,

		@Schema(description = "Created at")
		LocalDateTime createdAt
) {

	public static LectureDetailResponse from(Lecture lecture, String professorName, boolean enrolled, String role) {
		return new LectureDetailResponse(
				lecture.getId(),
				lecture.getTitle(),
				professorName,
				lecture.getSemester(),
				lecture.getDescription(),
				enrolled,
				role,
				lecture.getCreatedAt()
		);
	}
}
