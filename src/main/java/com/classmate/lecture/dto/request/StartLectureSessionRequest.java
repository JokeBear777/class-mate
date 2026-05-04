package com.classmate.lecture.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Lecture session start request")
public record StartLectureSessionRequest(

		@Schema(description = "Session title", example = "Week 3 - Processes and Threads")
		@Size(max = 100, message = "Session title must be at most 100 characters.")
		String title
) {
}
