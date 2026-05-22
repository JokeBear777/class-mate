package com.classmate.note.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Session shared note block update request")
public record UpdateSessionNoteBlockRequest(
		@Schema(description = "New block content", example = "수정된 공동 필기 내용")
		@NotBlank
		@Size(max = 5000)
		String content,

		@Schema(description = "Client-known current block version. Must match server version.", example = "1")
		@Min(1)
		long version,

		@Schema(description = "Client tab or editor identifier", example = "browser-tab-abc123")
		String clientId
) {
}
