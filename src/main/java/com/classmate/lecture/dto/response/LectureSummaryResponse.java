package com.classmate.lecture.dto.response;

import com.classmate.lecture.domain.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lecture summary response")
public record LectureSummaryResponse(

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
		boolean enrolled
) {

	public static LectureSummaryResponse from(Lecture lecture, String professorName, boolean enrolled) {
		return new LectureSummaryResponse(
				lecture.getId(),
				lecture.getTitle(),
				professorName,
				lecture.getSemester(),
				lecture.getDescription(),
				enrolled
		);
	}
}
