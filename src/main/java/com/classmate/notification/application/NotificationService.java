package com.classmate.notification.application;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.monitoring.domain.MonitoringAlert;
import com.classmate.notification.domain.Notification;
import com.classmate.notification.domain.NotificationChannel;
import com.classmate.notification.domain.NotificationType;
import com.classmate.notification.dto.response.NotificationResponse;
import com.classmate.notification.infra.NotificationRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final Map<NotificationChannel, NotificationSender> senders;

	public NotificationService(
			NotificationRepository notificationRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			List<NotificationSender> senders
	) {
		this.notificationRepository = notificationRepository;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.senders = toSenderMap(senders);
	}

	@Transactional
	public NotificationResponse createFromMonitoringAlert(MonitoringAlert alert) {
		if (notificationRepository.existsBySourceAlertIdAndChannel(alert.getId(), NotificationChannel.WEBSOCKET)) {
			return notificationRepository.findBySessionIdOrderByCreatedAtDesc(alert.getSessionId())
					.stream()
					.filter(notification -> alert.getId().equals(notification.getSourceAlertId()))
					.filter(notification -> notification.getChannel() == NotificationChannel.WEBSOCKET)
					.findFirst()
					.map(NotificationResponse::from)
					.orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
		}

		Notification websocketNotification = createAndSend(alert, NotificationChannel.WEBSOCKET);
		return NotificationResponse.from(websocketNotification);
	}

	public List<NotificationResponse> getSessionNotifications(Long sessionId) {
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		validateProfessorOrAssistant(session.getLectureId());
		return notificationRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
				.stream()
				.map(NotificationResponse::from)
				.toList();
	}

	public List<NotificationResponse> getMyNotifications() {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(currentUserId)
				.stream()
				.map(NotificationResponse::from)
				.toList();
	}

	@Transactional
	public NotificationResponse markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
		validateReadAccess(notification);
		notification.markRead();
		return NotificationResponse.from(notification);
	}

	@Transactional
	public void markSessionNotificationsAsRead(Long sessionId) {
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		validateProfessorOrAssistant(session.getLectureId());
		notificationRepository.findBySessionIdAndReadAtIsNull(sessionId)
				.forEach(Notification::markRead);
	}

	private Notification createAndSend(MonitoringAlert alert, NotificationChannel channel) {
		Notification notification = notificationRepository.save(Notification.createSessionAlert(
				alert.getLectureId(),
				alert.getSessionId(),
				NotificationType.valueOf(alert.getAlertType().name()),
				channel,
				toTitle(alert),
				alert.getMessage(),
				alert.getId()
		));
		NotificationSender sender = senders.get(channel);
		if (sender == null) {
			notification.markFailed("Notification sender is not configured.");
			return notification;
		}
		try {
			sender.send(notification);
			notification.markSent();
		} catch (RuntimeException exception) {
			notification.markFailed(exception.getMessage());
		}
		return notification;
	}

	private void validateReadAccess(Notification notification) {
		Long currentUserId = currentUserProvider.getCurrentUserId();
		if (notification.getReceiverId() != null) {
			if (!notification.getReceiverId().equals(currentUserId)) {
				throw new BusinessException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
			}
			return;
		}
		if (notification.getSessionId() == null) {
			throw new BusinessException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
		}
		LectureSession session = lectureAccessChecker.getSessionOrThrow(notification.getSessionId());
		lectureAccessChecker.validateProfessorOrAssistant(session.getLectureId(), currentUserId);
	}

	private void validateProfessorOrAssistant(Long lectureId) {
		lectureAccessChecker.validateProfessorOrAssistant(lectureId, currentUserProvider.getCurrentUserId());
	}

	private String toTitle(MonitoringAlert alert) {
		return switch (alert.getAlertType()) {
			case QUESTION_SPIKE -> "Question spike detected";
			case CONFUSION_SPIKE -> "Confusion spike detected";
			case FAST_PACE_SPIKE -> "Fast pace spike detected";
			case UNANSWERED_QUESTION_ACCUMULATION -> "Unanswered questions accumulating";
			case HIGH_CONFUSION_SCORE -> "High confusion score detected";
		};
	}

	private Map<NotificationChannel, NotificationSender> toSenderMap(List<NotificationSender> senders) {
		Map<NotificationChannel, NotificationSender> senderMap = new EnumMap<>(NotificationChannel.class);
		for (NotificationSender sender : senders) {
			senderMap.put(sender.getChannel(), sender);
		}
		return senderMap;
	}
}
