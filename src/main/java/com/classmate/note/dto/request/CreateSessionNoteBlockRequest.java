package com.classmate.note.dto.request;

import com.classmate.note.domain.SessionNoteBlockType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Session shared note block creation request")
public record CreateSessionNoteBlockRequest(
		@Schema(description = "Block type", example = "PARAGRAPH")
		@NotNull
		SessionNoteBlockType blockType,

		@Schema(description = "Block content", example = "오늘 수업 핵심 개념 정리")
		@Size(max = 5000)
		String content,

		@Schema(description = "Block ID after which the new block is inserted. Null appends to the end.", example = "1")
		Long afterBlockId
) {
}
