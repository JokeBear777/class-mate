package com.classmate.notification.application;

import com.classmate.notification.domain.Notification;
import com.classmate.notification.domain.NotificationChannel;

public interface NotificationSender {

	NotificationChannel getChannel();

	void send(Notification notification);
}
