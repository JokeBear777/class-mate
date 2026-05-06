package com.classmate.monitoring.domain;

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
@Table(name = "monitoring_alerts")
public class MonitoringAlert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Enumerated(EnumType.STRING)
	@Column(name = "alert_type", nullable = false, length = 50)
	private AlertType alertType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private AlertSeverity severity;

	@Column(nullable = false, length = 500)
	private String message;

	@Column(nullable = false)
	private double detectedValue;

	@Column(nullable = false)
	private double thresholdValue;

	@Column(nullable = false)
	private boolean resolved;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime resolvedAt;

	protected MonitoringAlert() {
	}

	private MonitoringAlert(
			Long sessionId,
			Long lectureId,
			AlertType alertType,
			AlertSeverity severity,
			String message,
			double detectedValue,
			double thresholdValue
	) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.alertType = alertType;
		this.severity = severity;
		this.message = message;
		this.detectedValue = detectedValue;
		this.thresholdValue = thresholdValue;
		this.resolved = false;
	}

	public static MonitoringAlert create(
			Long sessionId,
			Long lectureId,
			AlertType alertType,
			AlertSeverity severity,
			String message,
			double detectedValue,
			double thresholdValue
	) {
		return new MonitoringAlert(sessionId, lectureId, alertType, severity, message, detectedValue, thresholdValue);
	}

	public void resolve() {
		this.resolved = true;
		this.resolvedAt = LocalDateTime.now();
	}

	@PrePersist
	void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public AlertType getAlertType() {
		return alertType;
	}

	public AlertSeverity getSeverity() {
		return severity;
	}

	public String getMessage() {
		return message;
	}

	public double getDetectedValue() {
		return detectedValue;
	}

	public double getThresholdValue() {
		return thresholdValue;
	}

	public boolean isResolved() {
		return resolved;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}
}
