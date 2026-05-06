package com.classmate.event.publisher;

import com.classmate.event.domain.ClassMateEventPayload;
import com.classmate.event.support.RedisStreamNames;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStreamDlqPublisher {

	private static final Logger log = LoggerFactory.getLogger(RedisStreamDlqPublisher.class);

	private final StringRedisTemplate redisTemplate;

	public RedisStreamDlqPublisher(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void publish(String originalStream, String originalRecordId, ClassMateEventPayload event, Exception exception) {
		try {
			Map<String, String> fields = new LinkedHashMap<>();
			fields.put("originalStream", originalStream);
			fields.put("originalRecordId", originalRecordId);
			fields.put("eventId", event.eventId());
			fields.put("eventType", event.eventType().name());
			fields.put("payloadJson", event.payloadJson());
			fields.put("errorMessage", exception.getMessage());
			fields.put("failedAt", LocalDateTime.now().toString());

			redisTemplate.opsForStream().add(RedisStreamNames.LECTURE_EVENTS_DLQ_STREAM, fields);
		} catch (RuntimeException dlqException) {
			log.warn("Failed to publish event to DLQ. originalStream={}, originalRecordId={}",
					originalStream, originalRecordId, dlqException);
		}
	}

	public void publishMalformed(
			String originalStream,
			String originalRecordId,
			Map<Object, Object> originalFields,
			Exception exception
	) {
		try {
			Map<String, String> fields = new LinkedHashMap<>();
			fields.put("originalStream", originalStream);
			fields.put("originalRecordId", originalRecordId);
			fields.put("eventId", valueOrEmpty(originalFields, "eventId"));
			fields.put("eventType", valueOrEmpty(originalFields, "eventType"));
			fields.put("payloadJson", valueOrEmpty(originalFields, "payloadJson"));
			fields.put("errorMessage", exception.getMessage());
			fields.put("failedAt", LocalDateTime.now().toString());

			redisTemplate.opsForStream().add(RedisStreamNames.LECTURE_EVENTS_DLQ_STREAM, fields);
		} catch (RuntimeException dlqException) {
			log.warn("Failed to publish malformed event to DLQ. originalStream={}, originalRecordId={}",
					originalStream, originalRecordId, dlqException);
		}
	}

	private String valueOrEmpty(Map<Object, Object> fields, String key) {
		Object value = fields.get(key);
		return value == null ? "" : value.toString();
	}
}
