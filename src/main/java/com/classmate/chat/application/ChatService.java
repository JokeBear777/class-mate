package com.classmate.chat.application;

import com.classmate.chat.domain.ChatMessage;
import com.classmate.chat.dto.request.SendChatMessageRequest;
import com.classmate.chat.dto.response.ChatMessageResponse;
import com.classmate.chat.infra.ChatMessageRepository;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.realtime.application.RealtimeMessageService;
import com.classmate.realtime.dto.RealtimeChatMessage;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional(readOnly = true)
public class ChatService {

	private static final Logger log = LoggerFactory.getLogger(ChatService.class);
	private static final int DEFAULT_RECENT_MESSAGE_LIMIT = 50;
	private static final int MAX_RECENT_MESSAGE_LIMIT = 100;
	private static final long MAX_CATCH_UP_RANGE = 200L;

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomSequenceService chatRoomSequenceService;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final RealtimeMessageService realtimeMessageService;

	public ChatService(
			ChatMessageRepository chatMessageRepository,
			ChatRoomSequenceService chatRoomSequenceService,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			RealtimeMessageService realtimeMessageService
	) {
		this.chatMessageRepository = chatMessageRepository;
		this.chatRoomSequenceService = chatRoomSequenceService;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.realtimeMessageService = realtimeMessageService;
	}

	@Transactional
	public ChatMessageResponse sendMessage(Long sessionId, SendChatMessageRequest request) {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		String currentUserName = currentUserProvider.getCurrentUserName();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateActiveSession(session);
		lectureAccessChecker.validateParticipant(session.getLectureId(), currentUserId);

		long roomSeq = chatRoomSequenceService.nextRoomSeq(session.getId());
		ChatMessage chatMessage;
		try {
			chatMessage = chatMessageRepository.saveAndFlush(ChatMessage.create(
					session.getId(),
					session.getLectureId(),
					currentUserId,
					currentUserName,
					roomSeq,
					request.content().trim()
			));
		} catch (RuntimeException exception) {
			log.warn("Chat message persistence failed after roomSeq issued. sessionId={}, roomSeq={}, senderId={}",
					session.getId(), roomSeq, currentUserId, exception);
			throw exception;
		}

		afterCommit(() -> realtimeMessageService.sendChatMessage(session.getId(), RealtimeChatMessage.from(chatMessage)));

		return ChatMessageResponse.from(chatMessage);
	}

	public List<ChatMessageResponse> getSessionMessages(Long sessionId) {
		return getSessionMessages(sessionId, DEFAULT_RECENT_MESSAGE_LIMIT);
	}

	public List<ChatMessageResponse> getSessionMessages(Long sessionId, int limit) {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateParticipant(session.getLectureId(), currentUserId);
		int normalizedLimit = normalizeRecentMessageLimit(limit);

		return chatMessageRepository.findBySessionIdOrderByRoomSeqDesc(
						sessionId,
						PageRequest.of(0, normalizedLimit)
				)
				.stream()
				.sorted(Comparator.comparing(ChatMessage::getRoomSeq))
				.map(ChatMessageResponse::from)
				.toList();
	}

	public List<ChatMessageResponse> catchUpMessages(Long sessionId, Long afterSeq, Long beforeSeq) {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateParticipant(session.getLectureId(), currentUserId);
		validateCatchUpRange(afterSeq, beforeSeq);

		return chatMessageRepository
				.findBySessionIdAndRoomSeqGreaterThanAndRoomSeqLessThanOrderByRoomSeqAsc(
						sessionId,
						afterSeq,
						beforeSeq
				)
				.stream()
				.map(ChatMessageResponse::from)
				.toList();
	}

	private int normalizeRecentMessageLimit(int limit) {
		if (limit < 1) {
			throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Chat message limit must be at least 1.");
		}
		return Math.min(limit, MAX_RECENT_MESSAGE_LIMIT);
	}

	private void validateCatchUpRange(Long afterSeq, Long beforeSeq) {
		if (afterSeq == null || beforeSeq == null || afterSeq < 0 || beforeSeq <= afterSeq) {
			throw new BusinessException(ErrorCode.INVALID_CHAT_SEQ_RANGE);
		}
		if (beforeSeq - afterSeq > MAX_CATCH_UP_RANGE) {
			throw new BusinessException(ErrorCode.CHAT_CATCH_UP_RANGE_TOO_LARGE);
		}
	}

	private void afterCommit(Runnable action) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			action.run();
			return;
		}
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				action.run();
			}
		});
	}
}
