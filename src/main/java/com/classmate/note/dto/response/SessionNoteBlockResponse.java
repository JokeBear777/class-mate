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

		@Schema(description = "Block type", example = "PARAGRAPH")
		String blockType,

		@Schema(description = "Block content")
		String content,

		@Schema(description = "Block optimistic lock version", example = "2")
		long version,

		@Schema(description = "Last editor name", example = "김민우")
		String updatedByName,

		@Schema(description = "Last updated time")
		LocalDateTime updatedAt
) {

	public static SessionNoteBlockResponse from(SessionNoteBlock block, String updatedByName) {
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
				block.getUpdatedAt()
		);
	}
}
