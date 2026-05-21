package com.classmate.chat.application;

import com.classmate.chat.infra.ChatMessageRepository;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomSequenceService {

	private static final Logger log = LoggerFactory.getLogger(ChatRoomSequenceService.class);
	private static final String KEY_PREFIX = "classmate:chat:session:";
	private static final String KEY_SUFFIX = ":seq";

	private final StringRedisTemplate redisTemplate;
	private final ChatMessageRepository chatMessageRepository;

	public ChatRoomSequenceService(
			StringRedisTemplate redisTemplate,
			ChatMessageRepository chatMessageRepository
	) {
		this.redisTemplate = redisTemplate;
		this.chatMessageRepository = chatMessageRepository;
	}

	public long nextRoomSeq(Long sessionId) {
		String key = sequenceKey(sessionId);
		try {
			initializeCounterIfAbsent(sessionId, key);
			Long next = redisTemplate.opsForValue().increment(key);
			if (next == null) {
				log.warn("Redis INCR returned null for chat room sequence. sessionId={}, key={}", sessionId, key);
				throw new BusinessException(ErrorCode.CHAT_SEQUENCE_ISSUE_FAILED);
			}
			log.debug("Issued chat room sequence. sessionId={}, roomSeq={}", sessionId, next);
			return next;
		} catch (BusinessException exception) {
			throw exception;
		} catch (RuntimeException exception) {
			log.warn("Failed to issue chat room sequence from Redis. sessionId={}, key={}", sessionId, key, exception);
			throw new BusinessException(ErrorCode.CHAT_SEQUENCE_ISSUE_FAILED);
		}
	}

	private void initializeCounterIfAbsent(Long sessionId, String key) {
		Boolean exists = redisTemplate.hasKey(key);
		if (Boolean.TRUE.equals(exists)) {
			return;
		}
		Long maxRoomSeq = chatMessageRepository.findMaxRoomSeqBySessionId(sessionId).orElse(0L);
		Boolean initialized = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(maxRoomSeq));
		if (Boolean.TRUE.equals(initialized)) {
			log.info("Initialized chat room sequence counter. sessionId={}, maxRoomSeq={}, key={}",
					sessionId, maxRoomSeq, key);
		}
	}

	private String sequenceKey(Long sessionId) {
		return KEY_PREFIX + sessionId + KEY_SUFFIX;
	}
}
