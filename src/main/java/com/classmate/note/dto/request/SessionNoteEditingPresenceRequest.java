package com.classmate.note.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Session note editing presence request")
public record SessionNoteEditingPresenceRequest(
		@Schema(description = "Client tab or editor identifier", example = "browser-tab-abc123")
		String clientId,

		@Schema(description = "Client draft sequence", example = "18")
		@Min(0)
		long draftSeq
) {
}
