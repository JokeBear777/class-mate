package com.classmate.lecture.dto.response;

import com.classmate.lecture.domain.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Lecture creation response")
public record LectureResponse(

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

		@Schema(description = "Lecture join code", example = "A1B2C3")
		String joinCode,

		@Schema(description = "Created at")
		LocalDateTime createdAt
) {

	public static LectureResponse from(Lecture lecture, String professorName) {
		return new LectureResponse(
				lecture.getId(),
				lecture.getTitle(),
				professorName,
				lecture.getSemester(),
				lecture.getDescription(),
				lecture.getJoinCode(),
				lecture.getCreatedAt()
		);
	}
}
