package com.classmate.note.application;

import com.classmate.auth.application.UserQueryService;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.note.domain.SessionNoteBlock;
import com.classmate.note.domain.SessionNoteBlockHistory;
import com.classmate.note.domain.SessionSharedNote;
import com.classmate.note.dto.message.RealtimeSessionNoteMessage;
import com.classmate.note.dto.message.SessionNoteRealtimeEventType;
import com.classmate.note.dto.request.CreateSessionNoteBlockRequest;
import com.classmate.note.dto.request.SessionNoteEditingPresenceRequest;
import com.classmate.note.dto.request.UpdateSessionNoteBlockRequest;
import com.classmate.note.dto.response.SessionNoteBlockConflictResponse;
import com.classmate.note.dto.response.SessionNoteBlockHistoryResponse;
import com.classmate.note.dto.response.SessionNoteBlockResponse;
import com.classmate.note.dto.response.SessionSharedNoteResponse;
import com.classmate.note.infra.SessionNoteBlockHistoryRepository;
import com.classmate.note.infra.SessionNoteBlockRepository;
import com.classmate.note.infra.SessionSharedNoteRepository;
import com.classmate.realtime.application.RealtimeMessageService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SessionSharedNoteService {

	private static final int BLOCK_ORDER_STEP = 1000;

	private final SessionSharedNoteRepository sessionSharedNoteRepository;
	private final SessionNoteBlockRepository sessionNoteBlockRepository;
	private final SessionNoteBlockHistoryRepository sessionNoteBlockHistoryRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final UserQueryService userQueryService;
	private final RealtimeMessageService realtimeMessageService;
	private final SessionNotePresenceService sessionNotePresenceService;

	public SessionSharedNoteService(
			SessionSharedNoteRepository sessionSharedNoteRepository,
			SessionNoteBlockRepository sessionNoteBlockRepository,
			SessionNoteBlockHistoryRepository sessionNoteBlockHistoryRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			UserQueryService userQueryService,
			RealtimeMessageService realtimeMessageService,
			SessionNotePresenceService sessionNotePresenceService
	) {
		this.sessionSharedNoteRepository = sessionSharedNoteRepository;
		this.sessionNoteBlockRepository = sessionNoteBlockRepository;
		this.sessionNoteBlockHistoryRepository = sessionNoteBlockHistoryRepository;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.userQueryService = userQueryService;
		this.realtimeMessageService = realtimeMessageService;
		this.sessionNotePresenceService = sessionNotePresenceService;
	}

	@Transactional
	public SessionSharedNoteResponse getOrCreateSessionSharedNote(Long sessionId) {
		Long currentUserId = currentUserId();
		LectureSession session = getSessionAndValidateParticipant(sessionId, currentUserId);
		SessionSharedNote note = getOrCreateNote(session);
		return toSharedNoteResponse(note);
	}

	@Transactional
	public SessionNoteBlockResponse createBlock(Long sessionId, CreateSessionNoteBlockRequest request) {
		Long currentUserId = currentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		LectureSession session = getSessionAndValidateParticipant(sessionId, currentUserId);
		SessionSharedNote note = getOrCreateNote(session);

		int blockOrder = calculateBlockOrder(note.getId(), request.afterBlockId());
		SessionNoteBlock block = sessionNoteBlockRepository.save(SessionNoteBlock.create(
				note.getId(),
				session.getId(),
				session.getLectureId(),
				blockOrder,
				request.blockType(),
				normalizeContent(request.content()),
				currentUserId
		));
		note.increaseRevision();

		realtimeMessageService.sendSessionNoteMessage(
				session.getId(),
				RealtimeSessionNoteMessage.fromBlock(
						SessionNoteRealtimeEventType.DOCUMENT_BLOCK_CREATED,
						block,
						note.getDocumentRevision(),
						null,
						currentUserName
				)
		);
		return SessionNoteBlockResponse.from(block, currentUserName);
	}

	public SessionNoteBlockResponse getBlock(Long sessionId, Long blockId) {
		Long currentUserId = currentUserId();
		LectureSession session = getSessionAndValidateParticipant(sessionId, currentUserId);
		SessionSharedNote note = getNoteOrThrow(session.getId());
		SessionNoteBlock block = getBlockOrThrow(note.getId(), blockId);
		return toBlockResponse(block);
	}

	@Transactional
	public SessionNoteBlockResponse updateBlock(Long sessionId, Long blockId, UpdateSessionNoteBlockRequest request) {
		Long currentUserId = currentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		LectureSession session = getSessionAndValidateParticipant(sessionId, currentUserId);
		SessionSharedNote note = getNoteOrThrow(session.getId());
		SessionNoteBlock block = getBlockOrThrow(note.getId(), blockId);

		if (block.getVersion() != request.version()) {
			throwVersionConflict(block, request.version());
		}

		String newContent = normalizeContent(request.content());
		if (block.getContent().equals(newContent)) {
			return SessionNoteBlockResponse.from(block, userName(block.getUpdatedBy()));
		}

		String previousContent = block.getContent();
		long previousVersion = block.getVersion();
		block.update(newContent, request.version(), currentUserId);
		note.increaseRevision();
		sessionNoteBlockHistoryRepository.save(SessionNoteBlockHistory.recordUpdate(
				block,
				previousContent,
				previousVersion,
				currentUserId
		));

		realtimeMessageService.sendSessionNoteMessage(
				session.getId(),
				RealtimeSessionNoteMessage.fromBlock(
						SessionNoteRealtimeEventType.DOCUMENT_BLOCK_SAVED,
						block,
						note.getDocumentRevision(),
						request.clientId(),
						currentUserName
				)
		);
		return SessionNoteBlockResponse.from(block, currentUserName);
	}

	@Transactional
	public void deleteBlock(Long sessionId, Long blockId) {
		Long currentUserId = currentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		LectureSession session = getSessionAndValidateParticipant(sessionId, currentUserId);
		SessionSharedNote note = getNoteOrThrow(session.getId());
		SessionNoteBlock block = getBlockOrThrow(note.getId(), blockId);

		if (!block.getCreatedBy().equals(currentUserId)
				&& !lectureAccessChecker.isProfessorOrAssistant(session.getLectureId(), currentUserId)) {
			throw new BusinessException(ErrorCode.SESSION_NOTE_BLOCK_ACCESS_DENIED);
		}

		block.delete(currentUserId);
		note.increaseRevision();

		realtimeMessageService.sendSessionNoteMessage(
				session.getId(),
				RealtimeSessionNoteMessage.fromBlock(
						SessionNoteRealtimeEventType.DOCUMENT_BLOCK_DELETED,
						block,
						note.getDocumentRevision(),
						null,
						currentUserName
				)
		);
	}

	public List<SessionNoteBlockHistoryResponse> getBlockHistories(Long sessionId, Long blockId) {
		Long currentUserId = currentUserId();
		LectureSession session = getSessionAndValidateParticipant(sessionId, currentUserId);
		SessionSharedNote note = getNoteOrThrow(session.getId());
		getBlockOrThrow(note.getId(), blockId);

		return sessionNoteBlockHistoryRepository.findByBlockIdOrderByCreatedAtDesc(blockId)
				.stream()
				.map(history -> SessionNoteBlockHistoryResponse.from(history, userName(history.getEditedBy())))
				.toList();
	}

	public void startEditing(Long sessionId, Long blockId, SessionNoteEditingPresenceRequest request) {
		Long currentUserId = currentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		SessionNoteBlock block = getBlockAfterParticipantValidation(sessionId, blockId, currentUserId);
		sessionNotePresenceService.startEditing(block, currentUserId, currentUserName, request);
	}

	public void heartbeat(Long sessionId, Long blockId, SessionNoteEditingPresenceRequest request) {
		Long currentUserId = currentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		SessionNoteBlock block = getBlockAfterParticipantValidation(sessionId, blockId, currentUserId);
		sessionNotePresenceService.heartbeat(block, currentUserId, currentUserName, request);
	}

	public void stopEditing(Long sessionId, Long blockId, SessionNoteEditingPresenceRequest request) {
		Long currentUserId = currentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		SessionNoteBlock block = getBlockAfterParticipantValidation(sessionId, blockId, currentUserId);
		sessionNotePresenceService.stopEditing(block, request, currentUserName);
	}

	public void broadcastDraftChanged(Long sessionId, Long userId, RealtimeSessionNoteMessage message) {
		LectureSession session = getSessionAndValidateParticipant(sessionId, userId);
		SessionSharedNote note = getNoteOrThrow(session.getId());
		SessionNoteBlock block = getBlockOrThrow(note.getId(), message.blockId());
		String editorName = userName(userId);
		realtimeMessageService.sendSessionNoteMessage(
				session.getId(),
				RealtimeSessionNoteMessage.draftChanged(message, block, editorName)
		);
	}

	private LectureSession getSessionAndValidateParticipant(Long sessionId, Long userId) {
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateParticipant(session.getLectureId(), userId);
		return session;
	}

	private SessionNoteBlock getBlockAfterParticipantValidation(Long sessionId, Long blockId, Long userId) {
		LectureSession session = getSessionAndValidateParticipant(sessionId, userId);
		SessionSharedNote note = getNoteOrThrow(session.getId());
		return getBlockOrThrow(note.getId(), blockId);
	}

	private SessionSharedNote getOrCreateNote(LectureSession session) {
		return sessionSharedNoteRepository.findBySessionId(session.getId())
				.orElseGet(() -> sessionSharedNoteRepository.save(SessionSharedNote.create(
						session.getId(),
						session.getLectureId(),
						session.getTitle() + " shared note"
				)));
	}

	private SessionSharedNote getNoteOrThrow(Long sessionId) {
		return sessionSharedNoteRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new BusinessException(ErrorCode.SESSION_SHARED_NOTE_NOT_FOUND));
	}

	private SessionNoteBlock getBlockOrThrow(Long noteId, Long blockId) {
		return sessionNoteBlockRepository.findByIdAndNoteIdAndDeletedFalse(blockId, noteId)
				.orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOTE_BLOCK_NOT_FOUND));
	}

	private int calculateBlockOrder(Long noteId, Long afterBlockId) {
		if (afterBlockId == null) {
			return sessionNoteBlockRepository.findTopByNoteIdAndDeletedFalseOrderByBlockOrderDesc(noteId)
					.map(block -> block.getBlockOrder() + BLOCK_ORDER_STEP)
					.orElse(BLOCK_ORDER_STEP);
		}

		SessionNoteBlock afterBlock = getBlockOrThrow(noteId, afterBlockId);
		return sessionNoteBlockRepository
				.findFirstByNoteIdAndDeletedFalseAndBlockOrderGreaterThanOrderByBlockOrderAsc(
						noteId,
						afterBlock.getBlockOrder()
				)
				.map(nextBlock -> {
					int gap = nextBlock.getBlockOrder() - afterBlock.getBlockOrder();
					if (gap <= 1) {
						throw new BusinessException(ErrorCode.SESSION_NOTE_INVALID_BLOCK_ORDER);
					}
					return afterBlock.getBlockOrder() + gap / 2;
				})
				.orElse(afterBlock.getBlockOrder() + BLOCK_ORDER_STEP);
	}

	private SessionSharedNoteResponse toSharedNoteResponse(SessionSharedNote note) {
		List<SessionNoteBlockResponse> blocks = sessionNoteBlockRepository
				.findByNoteIdAndDeletedFalseOrderByBlockOrderAsc(note.getId())
				.stream()
				.map(this::toBlockResponse)
				.toList();
		return SessionSharedNoteResponse.from(note, blocks);
	}

	private SessionNoteBlockResponse toBlockResponse(SessionNoteBlock block) {
		return SessionNoteBlockResponse.from(block, userName(block.getUpdatedBy()));
	}

	private void throwVersionConflict(SessionNoteBlock block, long requestVersion) {
		SessionNoteBlockConflictResponse conflictResponse = new SessionNoteBlockConflictResponse(
				block.getId(),
				requestVersion,
				block.getVersion(),
				block.getContent(),
				userName(block.getUpdatedBy()),
				block.getUpdatedAt()
		);
		throw new BusinessException(
				ErrorCode.SESSION_NOTE_BLOCK_VERSION_CONFLICT,
				ErrorCode.SESSION_NOTE_BLOCK_VERSION_CONFLICT.getMessage(),
				conflictResponse
		);
	}

	private String userName(Long userId) {
		return userQueryService.getUserName(userId);
	}

	private String normalizeContent(String content) {
		return content == null ? "" : content.trim();
	}

	private Long currentUserId() {
		return currentUserProvider.getCurrentUserId();
	}
}
