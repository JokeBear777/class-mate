package com.classmate.board.api;

import com.classmate.board.application.BoardService;
import com.classmate.board.domain.PostType;
import com.classmate.board.dto.request.CreatePostRequest;
import com.classmate.board.dto.request.UpdatePostRequest;
import com.classmate.board.dto.response.PostResponse;
import com.classmate.board.dto.response.PostSummaryResponse;
import com.classmate.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "Lecture board post APIs")
@RestController
@RequestMapping("/api/v1")
public class BoardController {

	private final BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@Operation(summary = "Create post", description = "Creates a board post for a lecture. Lecture participant access is required.")
	@PostMapping("/lectures/{lectureId}/posts")
	public ApiResponse<PostResponse> createPost(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId,
			@Valid @RequestBody CreatePostRequest request
	) {
		return ApiResponse.success("Post created.", boardService.createPost(lectureId, request));
	}

	@Operation(summary = "Get lecture posts", description = "Returns non-deleted board posts for a lecture.")
	@GetMapping("/lectures/{lectureId}/posts")
	public ApiResponse<List<PostSummaryResponse>> getLecturePosts(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId,
			@Parameter(description = "Post type filter", example = "QUESTION")
			@RequestParam(required = false) PostType postType
	) {
		return ApiResponse.success(boardService.getLecturePosts(lectureId, postType));
	}

	@Operation(summary = "Get post detail", description = "Returns a board post detail. Lecture participant access is required.")
	@GetMapping("/posts/{postId}")
	public ApiResponse<PostResponse> getPost(
			@Parameter(description = "Post ID", example = "1")
			@PathVariable Long postId
	) {
		return ApiResponse.success(boardService.getPost(postId));
	}

	@Operation(summary = "Update post", description = "Updates a board post. Author access is required.")
	@PatchMapping("/posts/{postId}")
	public ApiResponse<PostResponse> updatePost(
			@Parameter(description = "Post ID", example = "1")
			@PathVariable Long postId,
			@Valid @RequestBody UpdatePostRequest request
	) {
		return ApiResponse.success("Post updated.", boardService.updatePost(postId, request));
	}

	@Operation(summary = "Delete post", description = "Soft deletes a board post. Author, professor, or assistant access is required.")
	@DeleteMapping("/posts/{postId}")
	public ApiResponse<Void> deletePost(
			@Parameter(description = "Post ID", example = "1")
			@PathVariable Long postId
	) {
		boardService.deletePost(postId);
		return ApiResponse.success("Post deleted.", null);
	}
}
