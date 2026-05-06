package com.classmate.event.domain;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public record ClassMateEventPayload(
		String eventId,
		ClassMateEventType eventType,
		String aggregateType,
		Long aggregateId,
		Long lectureId,
		Long sessionId,
		LocalDateTime occurredAt,
		String payloadJson
) {

	public static ClassMateEventPayload create(
			ClassMateEventType eventType,
			String aggregateType,
			Long aggregateId,
			Long lectureId,
			Long sessionId,
			String payloadJson
	) {
		return new ClassMateEventPayload(
				UUID.randomUUID().toString(),
				eventType,
				aggregateType,
				aggregateId,
				lectureId,
				sessionId,
				LocalDateTime.now(),
				payloadJson
		);
	}

	public Map<String, String> toRecord() {
		Map<String, String> fields = new LinkedHashMap<>();
		fields.put("eventId", eventId);
		fields.put("eventType", eventType.name());
		fields.put("aggregateType", aggregateType);
		fields.put("aggregateId", String.valueOf(aggregateId));
		fields.put("lectureId", String.valueOf(lectureId));
		fields.put("sessionId", String.valueOf(sessionId));
		fields.put("occurredAt", occurredAt.toString());
		fields.put("payloadJson", payloadJson);
		return fields;
	}

	public static ClassMateEventPayload fromRecord(Map<Object, Object> fields) {
		return new ClassMateEventPayload(
				value(fields, "eventId"),
				ClassMateEventType.valueOf(value(fields, "eventType")),
				value(fields, "aggregateType"),
				Long.valueOf(value(fields, "aggregateId")),
				Long.valueOf(value(fields, "lectureId")),
				Long.valueOf(value(fields, "sessionId")),
				LocalDateTime.parse(value(fields, "occurredAt")),
				value(fields, "payloadJson")
		);
	}

	private static String value(Map<Object, Object> fields, String key) {
		Object value = fields.get(key);
		if (value == null) {
			throw new IllegalStateException("Missing event field: " + key);
		}
		return value.toString();
	}
}
