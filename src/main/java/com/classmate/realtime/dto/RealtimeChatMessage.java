package com.classmate.realtime.dto;

import com.classmate.chat.domain.ChatMessage;
import java.time.LocalDateTime;

public record RealtimeChatMessage(
		RealtimeEventType eventType,
		Long chatMessageId,
		Long sessionId,
		Long lectureId,
		String senderName,
		String content,
		LocalDateTime createdAt
) {

	public static RealtimeChatMessage from(ChatMessage chatMessage) {
		return new RealtimeChatMessage(
				RealtimeEventType.CHAT_MESSAGE_SENT,
				chatMessage.getId(),
				chatMessage.getSessionId(),
				chatMessage.getLectureId(),
				chatMessage.getSenderName(),
				chatMessage.getContent(),
				chatMessage.getCreatedAt()
		);
	}
}
