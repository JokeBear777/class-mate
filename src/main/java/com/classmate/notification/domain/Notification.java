package com.classmate.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "session_id")
	private Long sessionId;

	@Column(name = "receiver_id")
	private Long receiverId;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", nullable = false, length = 50)
	private NotificationType notificationType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private NotificationChannel channel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private NotificationStatus status;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String message;

	@Column(name = "source_alert_id")
	private Long sourceAlertId;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@Column(name = "failed_reason", length = 500)
	private String failedReason;

	protected Notification() {
	}

	private Notification(
			Long lectureId,
			Long sessionId,
			Long receiverId,
			NotificationType notificationType,
			NotificationChannel channel,
			String title,
			String message,
			Long sourceAlertId
	) {
		this.lectureId = lectureId;
		this.sessionId = sessionId;
		this.receiverId = receiverId;
		this.notificationType = notificationType;
		this.channel = channel;
		this.status = NotificationStatus.PENDING;
		this.title = title;
		this.message = message;
		this.sourceAlertId = sourceAlertId;
	}

	public static Notification createSessionAlert(
			Long lectureId,
			Long sessionId,
			NotificationType notificationType,
			NotificationChannel channel,
			String title,
			String message,
			Long sourceAlertId
	) {
		return new Notification(lectureId, sessionId, null, notificationType, channel, title, message, sourceAlertId);
	}

	public void markSent() {
		this.status = NotificationStatus.SENT;
		this.sentAt = LocalDateTime.now();
		this.failedReason = null;
	}

	public void markFailed(String reason) {
		this.status = NotificationStatus.FAILED;
		this.failedReason = reason;
	}

	public void markRead() {
		if (this.readAt == null) {
			this.readAt = LocalDateTime.now();
		}
	}

	@PrePersist
	void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public NotificationChannel getChannel() {
		return channel;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public Long getSourceAlertId() {
		return sourceAlertId;
	}

	public LocalDateTime getReadAt() {
		return readAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public String getFailedReason() {
		return failedReason;
	}
}
