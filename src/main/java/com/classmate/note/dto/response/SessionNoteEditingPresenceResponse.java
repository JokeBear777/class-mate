package com.classmate.note.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current Redis editing presence for a shared note block")
public record SessionNoteEditingPresenceResponse(
		@Schema(description = "Whether an editing presence exists for this block", example = "true")
		boolean present,

		@Schema(description = "Current editor display name when presence exists", example = "Kim Minwoo", nullable = true)
		String editorName,

		@Schema(description = "Client tab/device ID that owns the presence when provided", example = "browser-tab-abc123", nullable = true)
		String clientId,

		@Schema(description = "Remaining Redis presence TTL in seconds", example = "12", nullable = true)
		Long expiresInSeconds
) {

	public static SessionNoteEditingPresenceResponse inactive() {
		return new SessionNoteEditingPresenceResponse(false, null, null, null);
	}

	public static SessionNoteEditingPresenceResponse active(String editorName, String clientId, long expiresInSeconds) {
		return new SessionNoteEditingPresenceResponse(true, editorName, clientId, expiresInSeconds);
	}
}
