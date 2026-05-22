package com.classmate.note.application;

import com.classmate.note.domain.SessionNoteBlock;
import com.classmate.note.dto.message.RealtimeSessionNoteMessage;
import com.classmate.note.dto.message.SessionNoteRealtimeEventType;
import com.classmate.note.dto.request.SessionNoteEditingPresenceRequest;
import com.classmate.realtime.application.RealtimeMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionNotePresenceService {

	private static final Logger log = LoggerFactory.getLogger(SessionNotePresenceService.class);
	private static final Duration EDITING_PRESENCE_TTL = Duration.ofSeconds(15);

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final RealtimeMessageService realtimeMessageService;

	public SessionNotePresenceService(
			StringRedisTemplate redisTemplate,
			ObjectMapper objectMapper,
			RealtimeMessageService realtimeMessageService
	) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
		this.realtimeMessageService = realtimeMessageService;
	}

	public void startEditing(SessionNoteBlock block, Long userId, String userName, SessionNoteEditingPresenceRequest request) {
		savePresence(block, userId, userName, request);
		realtimeMessageService.sendSessionNoteMessage(
				block.getSessionId(),
				RealtimeSessionNoteMessage.presence(
						SessionNoteRealtimeEventType.DOCUMENT_BLOCK_EDITING_STARTED,
						block,
						request.draftSeq(),
						request.clientId(),
						userName
				)
		);
	}

	public void heartbeat(SessionNoteBlock block, Long userId, String userName, SessionNoteEditingPresenceRequest request) {
		savePresence(block, userId, userName, request);
		realtimeMessageService.sendSessionNoteMessage(
				block.getSessionId(),
				RealtimeSessionNoteMessage.presence(
						SessionNoteRealtimeEventType.DOCUMENT_BLOCK_EDITING_HEARTBEAT,
						block,
						request.draftSeq(),
						request.clientId(),
						userName
				)
		);
	}

	public void stopEditing(SessionNoteBlock block, SessionNoteEditingPresenceRequest request, String userName) {
		redisTemplate.delete(key(block.getSessionId(), block.getId()));
		realtimeMessageService.sendSessionNoteMessage(
				block.getSessionId(),
				RealtimeSessionNoteMessage.presence(
						SessionNoteRealtimeEventType.DOCUMENT_BLOCK_EDITING_STOPPED,
						block,
						request.draftSeq(),
						request.clientId(),
						userName
				)
		);
	}

	private void savePresence(
			SessionNoteBlock block,
			Long userId,
			String userName,
			SessionNoteEditingPresenceRequest request
	) {
		try {
			EditingPresenceValue value = new EditingPresenceValue(
					userId,
					userName,
					request.clientId(),
					request.draftSeq()
			);
			redisTemplate.opsForValue().set(
					key(block.getSessionId(), block.getId()),
					objectMapper.writeValueAsString(value),
					EDITING_PRESENCE_TTL
			);
		} catch (JsonProcessingException exception) {
			log.warn("Failed to serialize session note editing presence. sessionId={}, blockId={}",
					block.getSessionId(), block.getId(), exception);
		}
	}

	private String key(Long sessionId, Long blockId) {
		return "session-note:session:" + sessionId + ":block:" + blockId + ":editor";
	}

	private record EditingPresenceValue(
			Long userId,
			String userName,
			String clientId,
			long draftSeq
	) {
	}
}
