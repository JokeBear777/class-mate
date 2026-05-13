package com.classmate.notification.dto.response;

import com.classmate.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Notification response")
public record NotificationResponse(

		@Schema(description = "Notification ID", example = "1")
		Long notificationId,

		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Receiver user ID. Null means session-wide notification.", example = "3")
		Long receiverId,

		@Schema(description = "Notification type", example = "CONFUSION_SPIKE")
		String notificationType,

		@Schema(description = "Notification channel", example = "WEBSOCKET")
		String channel,

		@Schema(description = "Send status", example = "SENT")
		String status,

		@Schema(description = "Notification title", example = "Confusion spike detected")
		String title,

		@Schema(description = "Notification message", example = "Confused feedback is increasing.")
		String message,

		@Schema(description = "Source monitoring alert ID", example = "10")
		Long sourceAlertId,

		@Schema(description = "Whether the notification has been read", example = "false")
		boolean read,

		@Schema(description = "Created time")
		LocalDateTime createdAt,

		@Schema(description = "Sent time")
		LocalDateTime sentAt,

		@Schema(description = "Read time")
		LocalDateTime readAt,

		@Schema(description = "Failure reason")
		String failedReason
) {

	public static NotificationResponse from(Notification notification) {
		return new NotificationResponse(
				notification.getId(),
				notification.getLectureId(),
				notification.getSessionId(),
				notification.getReceiverId(),
				notification.getNotificationType().name(),
				notification.getChannel().name(),
				notification.getStatus().name(),
				notification.getTitle(),
				notification.getMessage(),
				notification.getSourceAlertId(),
				notification.getReadAt() != null,
				notification.getCreatedAt(),
				notification.getSentAt(),
				notification.getReadAt(),
				notification.getFailedReason()
		);
	}
}
