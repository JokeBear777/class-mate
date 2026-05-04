package com.classmate.lecture.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.lecture.application.LectureSessionService;
import com.classmate.lecture.dto.response.LectureSessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lecture Session", description = "Lecture session lifecycle APIs")
@RestController
@RequestMapping("/api/v1/sessions")
public class LectureSessionController {

	private final LectureSessionService lectureSessionService;

	public LectureSessionController(LectureSessionService lectureSessionService) {
		this.lectureSessionService = lectureSessionService;
	}

	@Operation(summary = "End lecture session", description = "Ends an active lecture session.")
	@PatchMapping("/{sessionId}/end")
	public ApiResponse<LectureSessionResponse> endSession(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success("Lecture session ended.", lectureSessionService.endSession(sessionId));
	}
}
