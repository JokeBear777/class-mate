package com.classmate.notification.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.notification.application.NotificationService;
import com.classmate.notification.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "Lecture session notification APIs")
@RestController
@RequestMapping("/api/v1")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Operation(
			summary = "Get session notifications",
			description = "Returns notifications for a session. Professor or assistant access is required."
	)
	@GetMapping("/sessions/{sessionId}/notifications")
	public ApiResponse<List<NotificationResponse>> getSessionNotifications(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(notificationService.getSessionNotifications(sessionId));
	}

	@Operation(
			summary = "Get my notifications",
			description = "Returns notifications addressed to the current user."
	)
	@GetMapping("/notifications/me")
	public ApiResponse<List<NotificationResponse>> getMyNotifications() {
		return ApiResponse.success(notificationService.getMyNotifications());
	}

	@Operation(
			summary = "Mark notification as read",
			description = "Marks a notification as read if the current user can access it."
	)
	@PatchMapping("/notifications/{notificationId}/read")
	public ApiResponse<NotificationResponse> markAsRead(
			@Parameter(description = "Notification ID", example = "1")
			@PathVariable Long notificationId
	) {
		return ApiResponse.success("Notification marked as read.", notificationService.markAsRead(notificationId));
	}

	@Operation(
			summary = "Mark session notifications as read",
			description = "Marks all unread notifications for a session as read. Professor or assistant access is required."
	)
	@PatchMapping("/sessions/{sessionId}/notifications/read-all")
	public ApiResponse<Void> markSessionNotificationsAsRead(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		notificationService.markSessionNotificationsAsRead(sessionId);
		return ApiResponse.success("Session notifications marked as read.", null);
	}
}
