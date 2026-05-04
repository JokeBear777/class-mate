package com.classmate.lecture.dto.response;

import com.classmate.lecture.domain.Lecture;
import com.classmate.lecture.domain.LectureRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lecture join response")
public record LectureJoinResponse(

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Lecture title", example = "Operating Systems")
		String title,

		@Schema(description = "Professor name", example = "Professor Kim")
		String professorName,

		@Schema(description = "Role assigned to the current user", example = "STUDENT")
		String role
) {

	public static LectureJoinResponse from(Lecture lecture, String professorName, LectureRole role) {
		return new LectureJoinResponse(lecture.getId(), lecture.getTitle(), professorName, role.name());
	}
}
