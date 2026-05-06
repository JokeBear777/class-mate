package com.classmate.monitoring.dto.internal;

import com.classmate.event.domain.ClassMateEventType;
import java.time.LocalDateTime;

public record MetricUpdateCommand(
		ClassMateEventType eventType,
		Long lectureId,
		Long sessionId,
		Long aggregateId,
		String feedbackType,
		LocalDateTime occurredAt
) {
}
