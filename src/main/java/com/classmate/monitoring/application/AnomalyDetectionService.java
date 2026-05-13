package com.classmate.monitoring.application;

import com.classmate.monitoring.domain.AlertSeverity;
import com.classmate.monitoring.domain.AlertType;
import com.classmate.monitoring.domain.MonitoringAlert;
import com.classmate.monitoring.domain.SessionMetric;
import com.classmate.monitoring.infra.MonitoringAlertRepository;
import com.classmate.notification.application.NotificationCommandService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class AnomalyDetectionService {

	private static final long QUESTION_SPIKE_THRESHOLD = 10;
	private static final long CONFUSION_SPIKE_THRESHOLD = 5;
	private static final long FAST_PACE_SPIKE_THRESHOLD = 5;
	private static final long UNANSWERED_QUESTION_THRESHOLD = 5;
	private static final double HIGH_CONFUSION_SCORE_THRESHOLD = 10.0;

	private final MonitoringAlertRepository monitoringAlertRepository;
	private final NotificationCommandService notificationCommandService;

	public AnomalyDetectionService(
			MonitoringAlertRepository monitoringAlertRepository,
			NotificationCommandService notificationCommandService
	) {
		this.monitoringAlertRepository = monitoringAlertRepository;
		this.notificationCommandService = notificationCommandService;
	}

	public void detectAndSaveAlerts(SessionMetric metric) {
		detect(metric, AlertType.QUESTION_SPIKE, AlertSeverity.WARNING,
				"Questions are increasing quickly.",
				metric.getQuestionCount(),
				QUESTION_SPIKE_THRESHOLD);
		detect(metric, AlertType.CONFUSION_SPIKE, AlertSeverity.WARNING,
				"Confused feedback is increasing.",
				metric.getConfusedCount(),
				CONFUSION_SPIKE_THRESHOLD);
		detect(metric, AlertType.FAST_PACE_SPIKE, AlertSeverity.WARNING,
				"Fast pace feedback is increasing.",
				metric.getFastPaceCount(),
				FAST_PACE_SPIKE_THRESHOLD);
		detect(metric, AlertType.UNANSWERED_QUESTION_ACCUMULATION, AlertSeverity.WARNING,
				"Unanswered questions are accumulating.",
				metric.getWaitingQuestionCount(),
				UNANSWERED_QUESTION_THRESHOLD);
		detect(metric, AlertType.HIGH_CONFUSION_SCORE, AlertSeverity.CRITICAL,
				"Confusion score is high. The lecture flow may need adjustment.",
				metric.getConfusionScore(),
				HIGH_CONFUSION_SCORE_THRESHOLD);
	}

	private void detect(
			SessionMetric metric,
			AlertType alertType,
			AlertSeverity severity,
			String message,
			double detectedValue,
			double thresholdValue
	) {
		if (detectedValue < thresholdValue) {
			return;
		}
		LocalDateTime duplicateWindowStart = LocalDateTime.now().minusMinutes(1);
		if (monitoringAlertRepository.existsBySessionIdAndAlertTypeAndCreatedAtAfter(
				metric.getSessionId(),
				alertType,
				duplicateWindowStart
		)) {
			return;
		}

		MonitoringAlert alert = monitoringAlertRepository.save(MonitoringAlert.create(
				metric.getSessionId(),
				metric.getLectureId(),
				alertType,
				severity,
				message,
				detectedValue,
				thresholdValue
		));
		notificationCommandService.notifyMonitoringAlert(alert);
		// TODO: Request LLM summary after LLM monitoring module is implemented.
	}
}
