package com.classmate.notification.application;

import com.classmate.monitoring.domain.MonitoringAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationCommandService {

	private static final Logger log = LoggerFactory.getLogger(NotificationCommandService.class);

	private final NotificationService notificationService;

	public NotificationCommandService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public void notifyMonitoringAlert(MonitoringAlert alert) {
		try {
			notificationService.createFromMonitoringAlert(alert);
			// TODO: Replace in-process NotificationCommandService call with gRPC when Notification module is separated.
		} catch (RuntimeException exception) {
			log.warn("Failed to create notification for monitoring alert. alertId={}", alert.getId(), exception);
		}
	}
}
