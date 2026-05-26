package com.classmate.note.dto.response;

import com.classmate.note.domain.SessionNoteBlock;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Session shared note block response")
public record SessionNoteBlockResponse(
		@Schema(description = "Block ID", example = "1")
		Long blockId,

		@Schema(description = "Shared note ID", example = "1")
		Long noteId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Block order", example = "1000")
		int blockOrder,

		@Schema(description = "Internal block type metadata for frontend rendering; not a user-facing label", example = "PARAGRAPH")
		String blockType,

		@Schema(description = "Block content")
		String content,

		@Schema(description = "Internal optimistic lock version used for update/delete conflict detection; not a user-facing label", example = "2")
		long version,

		@Schema(description = "Last editor name", example = "Kim Minwoo")
		String updatedByName,

		@Schema(description = "Last updated time")
		LocalDateTime updatedAt,

		@Schema(description = "Current Redis editing presence snapshot for this block")
		SessionNoteEditingPresenceResponse editingPresence
) {

	public static SessionNoteBlockResponse from(SessionNoteBlock block, String updatedByName) {
		return from(block, updatedByName, SessionNoteEditingPresenceResponse.inactive());
	}

	public static SessionNoteBlockResponse from(
			SessionNoteBlock block,
			String updatedByName,
			SessionNoteEditingPresenceResponse editingPresence
	) {
		return new SessionNoteBlockResponse(
				block.getId(),
				block.getNoteId(),
				block.getSessionId(),
				block.getLectureId(),
				block.getBlockOrder(),
				block.getBlockType().name(),
				block.getContent(),
				block.getVersion(),
				updatedByName,
				block.getUpdatedAt(),
				editingPresence
		);
	}
}
