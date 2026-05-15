package com.classmate.chat.application;

import com.classmate.chat.domain.ChatMessage;
import com.classmate.chat.dto.request.SendChatMessageRequest;
import com.classmate.chat.dto.response.ChatMessageResponse;
import com.classmate.chat.infra.ChatMessageRepository;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.realtime.application.RealtimeMessageService;
import com.classmate.realtime.dto.RealtimeChatMessage;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final RealtimeMessageService realtimeMessageService;

	public ChatService(
			ChatMessageRepository chatMessageRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			RealtimeMessageService realtimeMessageService
	) {
		this.chatMessageRepository = chatMessageRepository;
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

		ChatMessage chatMessage = chatMessageRepository.save(ChatMessage.create(
				session.getId(),
				session.getLectureId(),
				currentUserId,
				currentUserName,
				request.content().trim()
		));

		realtimeMessageService.sendChatMessage(session.getId(), RealtimeChatMessage.from(chatMessage));

		return ChatMessageResponse.from(chatMessage);
	}

	public List<ChatMessageResponse> getSessionMessages(Long sessionId) {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateParticipant(session.getLectureId(), currentUserId);

		return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
				.stream()
				.map(ChatMessageResponse::from)
				.toList();
	}
}
