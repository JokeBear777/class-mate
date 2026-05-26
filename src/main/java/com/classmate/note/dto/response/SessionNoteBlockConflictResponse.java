package com.classmate.note.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Session shared note block version conflict response")
public record SessionNoteBlockConflictResponse(
		@Schema(description = "Block ID", example = "1")
		Long blockId,

		@Schema(description = "Requested version", example = "1")
		long requestVersion,

		@Schema(description = "Current server version", example = "2")
		long currentVersion,

		@Schema(description = "Current server content")
		String currentContent,

		@Schema(description = "Current last editor name", example = "김민우")
		String currentUpdatedByName,

		@Schema(description = "Current last updated time")
		LocalDateTime currentUpdatedAt
) {
}
