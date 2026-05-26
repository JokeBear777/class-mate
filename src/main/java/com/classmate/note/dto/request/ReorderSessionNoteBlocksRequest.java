package com.classmate.note.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Session shared note block reorder request")
public record ReorderSessionNoteBlocksRequest(
		@Schema(description = "Ordered block IDs")
		@NotEmpty
		List<Long> blockIds
) {
}
