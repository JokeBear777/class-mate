package com.classmate.notification.infra;

import com.classmate.notification.application.NotificationSender;
import com.classmate.notification.domain.Notification;
import com.classmate.notification.domain.NotificationChannel;
import com.classmate.notification.dto.message.RealtimeNotificationMessage;
import com.classmate.realtime.application.RealtimeMessageService;
import org.springframework.stereotype.Component;

@Component
public class WebSocketNotificationSender implements NotificationSender {

	private final RealtimeMessageService realtimeMessageService;

	public WebSocketNotificationSender(RealtimeMessageService realtimeMessageService) {
		this.realtimeMessageService = realtimeMessageService;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.WEBSOCKET;
	}

	@Override
	public void send(Notification notification) {
		realtimeMessageService.sendAlertCreated(notification.getSessionId(), new RealtimeNotificationMessage(
				"ALERT_CREATED",
				notification.getId(),
				notification.getLectureId(),
				notification.getSessionId(),
				notification.getNotificationType().name(),
				null,
				notification.getTitle(),
				notification.getMessage(),
				notification.getCreatedAt()
		));
	}
}
