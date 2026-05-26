package com.classmate.note.api;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.response.ApiResponse;
import com.classmate.note.application.SessionSharedNoteService;
import com.classmate.note.dto.message.RealtimeSessionNoteMessage;
import com.classmate.note.dto.request.CreateSessionNoteBlockRequest;
import com.classmate.note.dto.request.SessionNoteEditingPresenceRequest;
import com.classmate.note.dto.request.UpdateSessionNoteBlockRequest;
import com.classmate.note.dto.response.SessionNoteBlockHistoryResponse;
import com.classmate.note.dto.response.SessionNoteBlockResponse;
import com.classmate.note.dto.response.SessionSharedNoteResponse;
import com.classmate.realtime.security.StompPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
		name = "Session Shared Note",
		description = """
				Session-scoped shared note APIs. GET /shared-note returns the DB snapshot.
				Snapshot blocks are ordered by blockOrder and include Redis editing presence when available.
				blockType and version are internal rendering/concurrency metadata, not user-facing labels.
				Block update requests must include the current block version; mismatches return 409 Conflict.
				DRAFT_CHANGED is a realtime UX event, not a DB save. BLOCK_SAVED is emitted only after DB save succeeds.
				Block histories record committed changes only.
				"""
)
@RestController
@RequestMapping("/api/v1")
public class SessionSharedNoteController {

	private static final Logger log = LoggerFactory.getLogger(SessionSharedNoteController.class);

	private final SessionSharedNoteService sessionSharedNoteService;

	public SessionSharedNoteController(SessionSharedNoteService sessionSharedNoteService) {
		this.sessionSharedNoteService = sessionSharedNoteService;
	}

	@Operation(
			summary = "Get session shared note snapshot",
			description = """
					Returns or creates a session-scoped shared note snapshot for lecture participants.
					Blocks are ordered by blockOrder and include current Redis editing presence per block.
					blockType and version are internal metadata for rendering and concurrency control.
					"""
	)
	@GetMapping("/sessions/{sessionId}/shared-note")
	public ApiResponse<SessionSharedNoteResponse> getSharedNote(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(sessionSharedNoteService.getOrCreateSessionSharedNote(sessionId));
	}

	@Operation(
			summary = "Create shared note block",
			description = "Creates a block in the session shared note and emits DOCUMENT_BLOCK_CREATED."
	)
	@PostMapping("/sessions/{sessionId}/shared-note/blocks")
	public ApiResponse<SessionNoteBlockResponse> createBlock(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Valid @RequestBody CreateSessionNoteBlockRequest request
	) {
		return ApiResponse.success("Session note block created.", sessionSharedNoteService.createBlock(sessionId, request));
	}

	@Operation(
			summary = "Get shared note block",
			description = "Returns a single non-deleted block from the session shared note."
	)
	@GetMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}")
	public ApiResponse<SessionNoteBlockResponse> getBlock(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId
	) {
		return ApiResponse.success(sessionSharedNoteService.getBlock(sessionId, blockId));
	}

	@Operation(
			summary = "Update shared note block",
			description = "Saves committed block content. Request version must match server version."
	)
	@PatchMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}")
	public ApiResponse<SessionNoteBlockResponse> updateBlock(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId,
			@Valid @RequestBody UpdateSessionNoteBlockRequest request
	) {
		return ApiResponse.success("Session note block saved.",
				sessionSharedNoteService.updateBlock(sessionId, blockId, request));
	}

	@Operation(
			summary = "Delete shared note block",
			description = """
					Deletes a block. Only the creator or lecture professor/assistant can delete it.
					Optional version query parameter enables conditional delete conflict detection.
					"""
	)
	@DeleteMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}")
	public ApiResponse<Void> deleteBlock(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId,
			@Parameter(description = "Client-known current block version. Optional for backward compatibility.", example = "2")
			@RequestParam(required = false) Long version
	) {
		sessionSharedNoteService.deleteBlock(sessionId, blockId, version);
		return ApiResponse.success("Session note block deleted.", null);
	}

	@Operation(
			summary = "Get shared note block histories",
			description = "Returns committed save histories for the block in descending creation order."
	)
	@GetMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}/histories")
	public ApiResponse<List<SessionNoteBlockHistoryResponse>> getBlockHistories(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId
	) {
		return ApiResponse.success(sessionSharedNoteService.getBlockHistories(sessionId, blockId));
	}

	@Operation(
			summary = "Start editing shared note block",
			description = """
					Stores Redis editing presence with 15s TTL and emits DOCUMENT_BLOCK_EDITING_STARTED.
					This is a UX soft lock and does not block REST save requests.
					"""
	)
	@PostMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}/editing/start")
	public ApiResponse<Void> startEditing(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId,
			@Valid @RequestBody SessionNoteEditingPresenceRequest request
	) {
		sessionSharedNoteService.startEditing(sessionId, blockId, request);
		return ApiResponse.success("Session note editing started.", null);
	}

	@Operation(
			summary = "Heartbeat shared note block editing",
			description = """
					Extends Redis editing presence TTL and emits DOCUMENT_BLOCK_EDITING_HEARTBEAT.
					This is a UX soft lock and does not block REST save requests.
					"""
	)
	@PostMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}/editing/heartbeat")
	public ApiResponse<Void> heartbeat(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId,
			@Valid @RequestBody SessionNoteEditingPresenceRequest request
	) {
		sessionSharedNoteService.heartbeat(sessionId, blockId, request);
		return ApiResponse.success("Session note editing heartbeat accepted.", null);
	}

	@Operation(
			summary = "Stop editing shared note block",
			description = """
					Deletes Redis editing presence and emits DOCUMENT_BLOCK_EDITING_STOPPED.
					This is a UX soft lock and does not block REST save requests.
					"""
	)
	@PostMapping("/sessions/{sessionId}/shared-note/blocks/{blockId}/editing/stop")
	public ApiResponse<Void> stopEditing(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId,
			@Parameter(description = "Block ID", example = "1")
			@PathVariable Long blockId,
			@Valid @RequestBody SessionNoteEditingPresenceRequest request
	) {
		sessionSharedNoteService.stopEditing(sessionId, blockId, request);
		return ApiResponse.success("Session note editing stopped.", null);
	}

	@MessageMapping("/sessions/{sessionId}/shared-note/draft")
	public void draftChanged(
			@DestinationVariable Long sessionId,
			@Payload RealtimeSessionNoteMessage message,
			Principal principal
	) {
		log.info(
				"Shared note draftChanged entered. sessionId={} principalNull={} principalClass={} blockId={} clientId={} draftSeq={}",
				sessionId,
				principal == null,
				principal == null ? null : principal.getClass().getName(),
				message == null ? null : message.blockId(),
				message == null ? null : message.clientId(),
				message == null ? null : message.draftSeq()
		);
		if (!(principal instanceof StompPrincipal stompPrincipal)) {
			throw new BusinessException(ErrorCode.UNAUTHENTICATED);
		}
		sessionSharedNoteService.broadcastDraftChanged(sessionId, stompPrincipal.getUserId(), message);
	}
}
