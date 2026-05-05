package com.classmate.feedback.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.feedback.application.FeedbackService;
import com.classmate.feedback.dto.request.CreateFeedbackRequest;
import com.classmate.feedback.dto.response.FeedbackResponse;
import com.classmate.feedback.dto.response.FeedbackSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feedback", description = "In-session realtime feedback APIs")
@RestController
@RequestMapping("/api/v1")
public class FeedbackController {

	private final FeedbackService feedbackService;

	public FeedbackController(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	@Operation(summary = "Submit feedback", description = "Submits feedback in an active lecture session.")
	@PostMapping("/sessions/{sessionId}/feedback")
	public ApiResponse<FeedbackResponse> createFeedback(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Valid @RequestBody CreateFeedbackRequest request
	) {
		return ApiResponse.success("Feedback submitted.", feedbackService.createFeedback(sessionId, request));
	}

	@Operation(summary = "Get feedback summary", description = "Returns feedback counts for a session. Professor or assistant access is required.")
	@GetMapping("/sessions/{sessionId}/feedback/summary")
	public ApiResponse<FeedbackSummaryResponse> getFeedbackSummary(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(feedbackService.getFeedbackSummary(sessionId));
	}
}
