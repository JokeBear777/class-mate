package com.classmate.chat.dto.response;

import com.classmate.chat.domain.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Chat message response")
public record ChatMessageResponse(

		@Schema(description = "Chat message ID", example = "1")
		Long chatMessageId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Sender display name", example = "Kim Minwoo")
		String senderName,

		@Schema(description = "Chat message content", example = "Could someone share the assignment link?")
		String content,

		@Schema(description = "Created time")
		LocalDateTime createdAt
) {

	public static ChatMessageResponse from(ChatMessage chatMessage) {
		return new ChatMessageResponse(
				chatMessage.getId(),
				chatMessage.getSessionId(),
				chatMessage.getLectureId(),
				chatMessage.getSenderName(),
				chatMessage.getContent(),
				chatMessage.getCreatedAt()
		);
	}
}
