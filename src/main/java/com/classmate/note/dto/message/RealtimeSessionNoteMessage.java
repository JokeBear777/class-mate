package com.classmate.note.dto.message;

import com.classmate.note.domain.SessionNoteBlock;
import java.time.LocalDateTime;

public record RealtimeSessionNoteMessage(
		String eventType,
		Long noteId,
		Long sessionId,
		Long lectureId,
		Long blockId,
		Integer blockOrder,
		String blockType,
		String content,
		Long version,
		Long documentRevision,
		Long draftSeq,
		String clientId,
		String editorName,
		LocalDateTime occurredAt
) {

	public static RealtimeSessionNoteMessage fromBlock(
			SessionNoteRealtimeEventType eventType,
			SessionNoteBlock block,
			Long documentRevision,
			String clientId,
			String editorName
	) {
		return new RealtimeSessionNoteMessage(
				eventType.name(),
				block.getNoteId(),
				block.getSessionId(),
				block.getLectureId(),
				block.getId(),
				block.getBlockOrder(),
				block.getBlockType().name(),
				block.getContent(),
				block.getVersion(),
				documentRevision,
				null,
				clientId,
				editorName,
				LocalDateTime.now()
		);
	}

	public static RealtimeSessionNoteMessage presence(
			SessionNoteRealtimeEventType eventType,
			SessionNoteBlock block,
			long draftSeq,
			String clientId,
			String editorName
	) {
		return new RealtimeSessionNoteMessage(
				eventType.name(),
				block.getNoteId(),
				block.getSessionId(),
				block.getLectureId(),
				block.getId(),
				block.getBlockOrder(),
				block.getBlockType().name(),
				null,
				block.getVersion(),
				null,
				draftSeq,
				clientId,
				editorName,
				LocalDateTime.now()
		);
	}

	public static RealtimeSessionNoteMessage draftChanged(
			RealtimeSessionNoteMessage message,
			SessionNoteBlock block,
			String editorName
	) {
		return new RealtimeSessionNoteMessage(
				SessionNoteRealtimeEventType.DOCUMENT_BLOCK_DRAFT_CHANGED.name(),
				block.getNoteId(),
				block.getSessionId(),
				block.getLectureId(),
				block.getId(),
				block.getBlockOrder(),
				block.getBlockType().name(),
				message.content(),
				block.getVersion(),
				null,
				message.draftSeq(),
				message.clientId(),
				editorName,
				LocalDateTime.now()
		);
	}
}
