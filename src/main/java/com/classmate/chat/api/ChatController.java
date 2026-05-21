package com.classmate.chat.api;

import com.classmate.chat.application.ChatService;
import com.classmate.chat.dto.request.SendChatMessageRequest;
import com.classmate.chat.dto.response.ChatMessageResponse;
import com.classmate.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Session Chat", description = "In-session realtime chat APIs")
@RestController
@RequestMapping("/api/v1")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@Operation(
			summary = "Send chat message",
			description = "Sends a chat message in an active lecture session. "
					+ "A Redis INCR based roomSeq is issued before persistence, and the WebSocket message is published only after DB save succeeds. "
					+ "roomSeq is a monotonic ordering key, not a guaranteed gapless sequence."
	)
	@PostMapping("/sessions/{sessionId}/chat/messages")
	public ApiResponse<ChatMessageResponse> sendMessage(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Valid @RequestBody SendChatMessageRequest request
	) {
		return ApiResponse.success("Chat message sent.", chatService.sendMessage(sessionId, request));
	}

	@Operation(
			summary = "Get session chat messages",
			description = "Returns the latest chat message snapshot for a lecture session in roomSeq ascending order. "
					+ "Lecture participant access is required. The default limit is 50 and values over 100 are capped to 100."
	)
	@GetMapping("/sessions/{sessionId}/chat/messages")
	public ApiResponse<List<ChatMessageResponse>> getSessionMessages(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Maximum number of recent messages to return", example = "50")
			@RequestParam(defaultValue = "50") int limit
	) {
		return ApiResponse.success(chatService.getSessionMessages(sessionId, limit));
	}

	@Operation(
			summary = "Catch up missing chat messages",
			description = "Returns messages with roomSeq greater than afterSeq and less than beforeSeq. "
					+ "An empty array is not an error; it means no messages were stored in that gap. "
					+ "Redis INCR based roomSeq is a monotonic ordering key, not a completely continuous sequence. "
					+ "The requested range must be at most 200."
	)
	@GetMapping("/sessions/{sessionId}/chat/messages/catch-up")
	public ApiResponse<List<ChatMessageResponse>> catchUpMessages(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Exclusive lower roomSeq bound", example = "102")
			@RequestParam Long afterSeq,
			@Parameter(description = "Exclusive upper roomSeq bound", example = "106")
			@RequestParam Long beforeSeq
	) {
		return ApiResponse.success(chatService.catchUpMessages(sessionId, afterSeq, beforeSeq));
	}
}
