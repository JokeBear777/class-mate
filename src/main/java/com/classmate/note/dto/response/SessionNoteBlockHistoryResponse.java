package com.classmate.note.dto.response;

import com.classmate.note.domain.SessionNoteBlockHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Session shared note block history response")
public record SessionNoteBlockHistoryResponse(
		@Schema(description = "History ID", example = "1")
		Long historyId,

		@Schema(description = "Block ID", example = "1")
		Long blockId,

		@Schema(description = "Previous content")
		String previousContent,

		@Schema(description = "New content")
		String newContent,

		@Schema(description = "Previous version", example = "1")
		long previousVersion,

		@Schema(description = "New version", example = "2")
		long newVersion,

		@Schema(description = "Editor name", example = "김민우")
		String editedByName,

		@Schema(description = "History creation time")
		LocalDateTime createdAt
) {

	public static SessionNoteBlockHistoryResponse from(SessionNoteBlockHistory history, String editedByName) {
		return new SessionNoteBlockHistoryResponse(
				history.getId(),
				history.getBlockId(),
				history.getPreviousContent(),
				history.getNewContent(),
				history.getPreviousVersion(),
				history.getNewVersion(),
				editedByName,
				history.getCreatedAt()
		);
	}
}
