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
			description = "Sends a chat message in an active lecture session. Lecture participant access is required."
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
			description = "Returns chat messages for a lecture session. Lecture participant access is required."
	)
	@GetMapping("/sessions/{sessionId}/chat/messages")
	public ApiResponse<List<ChatMessageResponse>> getSessionMessages(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(chatService.getSessionMessages(sessionId));
	}
}
