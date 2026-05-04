package com.classmate.lecture.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.lecture.application.LectureService;
import com.classmate.lecture.application.LectureSessionService;
import com.classmate.lecture.dto.request.CreateLectureRequest;
import com.classmate.lecture.dto.request.JoinLectureByCodeRequest;
import com.classmate.lecture.dto.request.StartLectureSessionRequest;
import com.classmate.lecture.dto.response.LectureDetailResponse;
import com.classmate.lecture.dto.response.LectureJoinResponse;
import com.classmate.lecture.dto.response.LectureResponse;
import com.classmate.lecture.dto.response.LectureSessionResponse;
import com.classmate.lecture.dto.response.LectureSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lecture", description = "Lecture creation, search, join, and session APIs")
@RestController
@RequestMapping("/api/v1/lectures")
public class LectureController {

	private final LectureService lectureService;
	private final LectureSessionService lectureSessionService;

	public LectureController(LectureService lectureService, LectureSessionService lectureSessionService) {
		this.lectureService = lectureService;
		this.lectureSessionService = lectureSessionService;
	}

	@Operation(summary = "Create lecture", description = "Creates a lecture and enrolls the current user as professor.")
	@PostMapping
	public ApiResponse<LectureResponse> createLecture(@Valid @RequestBody CreateLectureRequest request) {
		return ApiResponse.success("Lecture created.", lectureService.createLecture(request));
	}

	@Operation(summary = "Get my lectures", description = "Returns lectures the current user participates in.")
	@GetMapping("/me")
	public ApiResponse<List<LectureSummaryResponse>> getMyLectures() {
		return ApiResponse.success(lectureService.getMyLectures());
	}

	@Operation(summary = "Search lectures", description = "Searches lectures by title keyword and semester.")
	@GetMapping("/search")
	public ApiResponse<List<LectureSummaryResponse>> searchLectures(
			@Parameter(description = "Lecture title keyword", example = "Operating")
			@RequestParam(required = false) String keyword,
			@Parameter(description = "Semester", example = "2026-1")
			@RequestParam(required = false) String semester
	) {
		return ApiResponse.success(lectureService.searchLectures(keyword, semester));
	}

	@Operation(summary = "Get lecture detail", description = "Returns lecture detail without exposing the join code.")
	@GetMapping("/{lectureId}")
	public ApiResponse<LectureDetailResponse> getLectureDetail(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId
	) {
		return ApiResponse.success(lectureService.getLectureDetail(lectureId));
	}

	@Operation(summary = "Join lecture by ID and code", description = "Joins a lecture using the lecture ID and join code.")
	@PostMapping("/{lectureId}/join")
	public ApiResponse<LectureJoinResponse> joinLecture(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId,
			@Valid @RequestBody JoinLectureByCodeRequest request
	) {
		return ApiResponse.success("Lecture joined.", lectureService.joinLecture(lectureId, request));
	}

	@Operation(summary = "Join lecture by code", description = "Finds and joins a lecture using only the join code.")
	@PostMapping("/join-code")
	public ApiResponse<LectureJoinResponse> joinLectureByCode(@Valid @RequestBody JoinLectureByCodeRequest request) {
		return ApiResponse.success("Lecture joined.", lectureService.joinLectureByCode(request));
	}

	@Operation(summary = "Start lecture session", description = "Starts an active session for the lecture.")
	@PostMapping("/{lectureId}/sessions")
	public ApiResponse<LectureSessionResponse> startSession(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId,
			@Valid @RequestBody StartLectureSessionRequest request
	) {
		return ApiResponse.success("Lecture session started.", lectureSessionService.startSession(lectureId, request));
	}

	@Operation(summary = "Get lecture sessions", description = "Returns lecture sessions ordered by newest start time.")
	@GetMapping("/{lectureId}/sessions")
	public ApiResponse<List<LectureSessionResponse>> getLectureSessions(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId
	) {
		return ApiResponse.success(lectureSessionService.getLectureSessions(lectureId));
	}
}
