package com.classmate.note.dto.response;

import com.classmate.note.domain.SessionSharedNote;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Session shared note snapshot response")
public record SessionSharedNoteResponse(
		@Schema(description = "Shared note ID", example = "1")
		Long noteId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Shared note title", example = "Live lecture session 공동 필기")
		String title,

		@Schema(description = "Document revision increased after block create/update/delete", example = "3")
		long documentRevision,

		@Schema(description = "Non-deleted blocks ordered by blockOrder, including current Redis editing presence snapshot")
		List<SessionNoteBlockResponse> blocks
) {

	public static SessionSharedNoteResponse from(SessionSharedNote note, List<SessionNoteBlockResponse> blocks) {
		return new SessionSharedNoteResponse(
				note.getId(),
				note.getSessionId(),
				note.getLectureId(),
				note.getTitle(),
				note.getDocumentRevision(),
				blocks
		);
	}
}
