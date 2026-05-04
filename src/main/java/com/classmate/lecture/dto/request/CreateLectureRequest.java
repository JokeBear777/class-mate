package com.classmate.lecture.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Lecture creation request")
public record CreateLectureRequest(

		@Schema(description = "Lecture title", example = "Operating Systems")
		@NotBlank(message = "Lecture title is required.")
		@Size(max = 100, message = "Lecture title must be at most 100 characters.")
		String title,

		@Schema(description = "Semester", example = "2026-1")
		@NotBlank(message = "Semester is required.")
		@Size(max = 20, message = "Semester must be at most 20 characters.")
		String semester,

		@Schema(description = "Lecture description", example = "Core concepts of processes, memory, and file systems.")
		@Size(max = 500, message = "Lecture description must be at most 500 characters.")
		String description
) {
}
