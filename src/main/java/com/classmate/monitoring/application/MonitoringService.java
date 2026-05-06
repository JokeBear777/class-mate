package com.classmate.monitoring.application;

import com.classmate.common.security.CurrentUserProvider;
import com.classmate.event.domain.ClassMateEventType;
import com.classmate.event.dto.FeedbackSubmittedEvent;
import com.classmate.event.dto.QuestionCreatedEvent;
import com.classmate.feedback.domain.FeedbackType;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.monitoring.domain.MonitoringAlert;
import com.classmate.monitoring.domain.SessionMetric;
import com.classmate.monitoring.dto.response.LectureDashboardResponse;
import com.classmate.monitoring.dto.response.MonitoringAlertResponse;
import com.classmate.monitoring.dto.response.SessionDashboardResponse;
import com.classmate.monitoring.dto.response.SessionMetricResponse;
import com.classmate.monitoring.infra.MonitoringAlertRepository;
import com.classmate.monitoring.infra.SessionMetricRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MonitoringService {

	private final SessionMetricRepository sessionMetricRepository;
	private final MonitoringAlertRepository monitoringAlertRepository;
	private final AnomalyDetectionService anomalyDetectionService;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;

	public MonitoringService(
			SessionMetricRepository sessionMetricRepository,
			MonitoringAlertRepository monitoringAlertRepository,
			AnomalyDetectionService anomalyDetectionService,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker
	) {
		this.sessionMetricRepository = sessionMetricRepository;
		this.monitoringAlertRepository = monitoringAlertRepository;
		this.anomalyDetectionService = anomalyDetectionService;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
	}

	@Transactional
	public void handleQuestionCreated(QuestionCreatedEvent event) {
		SessionMetric metric = getOrCreateMetric(event.sessionId(), event.lectureId());
		metric.increaseQuestionCount(event.createdAt());
		anomalyDetectionService.detectAndSaveAlerts(metric);
		// TODO: Publish metric update to WebSocket after metric push module is implemented.
	}

	@Transactional
	public void handleFeedbackSubmitted(FeedbackSubmittedEvent event) {
		SessionMetric metric = getOrCreateMetric(event.sessionId(), event.lectureId());
		metric.increaseFeedbackCount(FeedbackType.valueOf(event.feedbackType()), event.createdAt());
		anomalyDetectionService.detectAndSaveAlerts(metric);
		// TODO: Publish metric update to WebSocket after metric push module is implemented.
	}

	public SessionDashboardResponse getSessionDashboard(Long sessionId) {
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		validateProfessorOrAssistant(session.getLectureId());
		SessionMetricResponse metric = sessionMetricRepository.findBySessionId(sessionId)
				.map(SessionMetricResponse::from)
				.orElseGet(() -> SessionMetricResponse.empty(sessionId, session.getLectureId()));
		List<MonitoringAlertResponse> alerts = getSessionAlertResponses(sessionId);
		return new SessionDashboardResponse(sessionId, session.getLectureId(), metric, alerts);
	}

	public LectureDashboardResponse getLectureDashboard(Long lectureId) {
		validateProfessorOrAssistant(lectureId);
		List<SessionMetricResponse> metrics = sessionMetricRepository.findByLectureIdOrderByUpdatedAtDesc(lectureId)
				.stream()
				.map(SessionMetricResponse::from)
				.toList();
		List<MonitoringAlertResponse> alerts = monitoringAlertRepository.findByLectureIdOrderByCreatedAtDesc(lectureId)
				.stream()
				.map(MonitoringAlertResponse::from)
				.toList();
		return new LectureDashboardResponse(lectureId, metrics, alerts);
	}

	public List<MonitoringAlertResponse> getSessionAlerts(Long sessionId) {
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		validateProfessorOrAssistant(session.getLectureId());
		return getSessionAlertResponses(sessionId);
	}

	private SessionMetric getOrCreateMetric(Long sessionId, Long lectureId) {
		return sessionMetricRepository.findBySessionId(sessionId)
				.orElseGet(() -> sessionMetricRepository.save(SessionMetric.initialize(sessionId, lectureId)));
	}

	private List<MonitoringAlertResponse> getSessionAlertResponses(Long sessionId) {
		return monitoringAlertRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
				.stream()
				.map(MonitoringAlertResponse::from)
				.toList();
	}

	private void validateProfessorOrAssistant(Long lectureId) {
		lectureAccessChecker.validateProfessorOrAssistant(lectureId, currentUserProvider.getCurrentUserId());
	}
}
