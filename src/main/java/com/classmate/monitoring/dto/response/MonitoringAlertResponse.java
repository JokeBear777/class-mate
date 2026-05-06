package com.classmate.monitoring.dto.response;

import com.classmate.monitoring.domain.MonitoringAlert;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Monitoring alert response")
public record MonitoringAlertResponse(
		@Schema(description = "Alert ID", example = "1")
		Long alertId,
		@Schema(description = "Session ID", example = "1")
		Long sessionId,
		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,
		@Schema(description = "Alert type", example = "CONFUSION_SPIKE")
		String alertType,
		@Schema(description = "Alert severity", example = "WARNING")
		String severity,
		@Schema(description = "Alert message", example = "Confused feedback is increasing.")
		String message,
		@Schema(description = "Detected value", example = "5")
		double detectedValue,
		@Schema(description = "Threshold value", example = "5")
		double thresholdValue,
		@Schema(description = "Whether alert is resolved", example = "false")
		boolean resolved,
		@Schema(description = "Created at")
		LocalDateTime createdAt,
		@Schema(description = "Resolved at")
		LocalDateTime resolvedAt
) {

	public static MonitoringAlertResponse from(MonitoringAlert alert) {
		return new MonitoringAlertResponse(
				alert.getId(),
				alert.getSessionId(),
				alert.getLectureId(),
				alert.getAlertType().name(),
				alert.getSeverity().name(),
				alert.getMessage(),
				alert.getDetectedValue(),
				alert.getThresholdValue(),
				alert.isResolved(),
				alert.getCreatedAt(),
				alert.getResolvedAt()
		);
	}
}
