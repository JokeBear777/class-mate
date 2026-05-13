package com.classmate.notification.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Realtime notification websocket message")
public record RealtimeNotificationMessage(

		@Schema(description = "Realtime event type", example = "ALERT_CREATED")
		String eventType,

		@Schema(description = "Notification ID", example = "1")
		Long notificationId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Notification type", example = "CONFUSION_SPIKE")
		String notificationType,

		@Schema(description = "Alert severity", example = "WARNING")
		String severity,

		@Schema(description = "Notification title", example = "Confusion spike detected")
		String title,

		@Schema(description = "Notification message", example = "Confused feedback is increasing.")
		String message,

		@Schema(description = "Created time")
		LocalDateTime createdAt
) {
}
