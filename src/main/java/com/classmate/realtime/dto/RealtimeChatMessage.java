package com.classmate.realtime.dto;

import com.classmate.chat.domain.ChatMessage;
import java.time.LocalDateTime;

public record RealtimeChatMessage(
		RealtimeEventType eventType,
		Long chatMessageId,
		Long messageId,
		Long sessionId,
		Long lectureId,
		Long roomSeq,
		String senderName,
		String content,
		LocalDateTime createdAt
) {

	public static RealtimeChatMessage from(ChatMessage chatMessage) {
		return new RealtimeChatMessage(
				RealtimeEventType.CHAT_MESSAGE_CREATED,
				chatMessage.getId(),
				chatMessage.getId(),
				chatMessage.getSessionId(),
				chatMessage.getLectureId(),
				chatMessage.getRoomSeq(),
				chatMessage.getSenderName(),
				chatMessage.getContent(),
				chatMessage.getCreatedAt()
		);
	}
}
