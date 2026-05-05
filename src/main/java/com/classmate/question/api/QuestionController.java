package com.classmate.question.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.question.application.QuestionService;
import com.classmate.question.dto.request.AnswerQuestionRequest;
import com.classmate.question.dto.request.CreateQuestionRequest;
import com.classmate.question.dto.response.QuestionResponse;
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

@Tag(name = "Question", description = "In-session anonymous question APIs")
@RestController
@RequestMapping("/api/v1")
public class QuestionController {

	private final QuestionService questionService;

	public QuestionController(QuestionService questionService) {
		this.questionService = questionService;
	}

	@Operation(summary = "Create question", description = "Creates an anonymous question in an active lecture session.")
	@PostMapping("/sessions/{sessionId}/questions")
	public ApiResponse<QuestionResponse> createQuestion(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Valid @RequestBody CreateQuestionRequest request
	) {
		return ApiResponse.success("Question created.", questionService.createQuestion(sessionId, request));
	}

	@Operation(summary = "Get session questions", description = "Returns questions for a session. Professor or assistant access is required.")
	@GetMapping("/sessions/{sessionId}/questions")
	public ApiResponse<List<QuestionResponse>> getSessionQuestions(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(questionService.getSessionQuestions(sessionId));
	}

	@Operation(summary = "Get question detail", description = "Returns a question detail. Professor or assistant access is required.")
	@GetMapping("/questions/{questionId}")
	public ApiResponse<QuestionResponse> getQuestion(
			@Parameter(description = "Question ID", example = "1")
			@PathVariable Long questionId
	) {
		return ApiResponse.success(questionService.getQuestion(questionId));
	}

	@Operation(summary = "Answer question", description = "Answers a question. Professor or assistant access is required.")
	@PatchMapping("/questions/{questionId}/answer")
	public ApiResponse<QuestionResponse> answerQuestion(
			@Parameter(description = "Question ID", example = "1")
			@PathVariable Long questionId,
			@Valid @RequestBody AnswerQuestionRequest request
	) {
		return ApiResponse.success("Question answered.", questionService.answerQuestion(questionId, request));
	}

	@Operation(summary = "Pin or unpin question", description = "Changes the pinned state of a question. Professor or assistant access is required.")
	@PatchMapping("/questions/{questionId}/pin")
	public ApiResponse<QuestionResponse> changePinned(
			@Parameter(description = "Question ID", example = "1")
			@PathVariable Long questionId,
			@Parameter(description = "Pinned state", example = "true")
			@RequestParam boolean pinned
	) {
		return ApiResponse.success("Question pinned state changed.", questionService.changePinned(questionId, pinned));
	}

	@Operation(summary = "Hide or show question", description = "Changes the hidden state of a question. Professor or assistant access is required.")
	@PatchMapping("/questions/{questionId}/hide")
	public ApiResponse<QuestionResponse> changeHidden(
			@Parameter(description = "Question ID", example = "1")
			@PathVariable Long questionId,
			@Parameter(description = "Hidden state", example = "true")
			@RequestParam boolean hidden
	) {
		return ApiResponse.success("Question hidden state changed.", questionService.changeHidden(questionId, hidden));
	}
}
