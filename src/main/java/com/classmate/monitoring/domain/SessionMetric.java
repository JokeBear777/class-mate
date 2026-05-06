package com.classmate.monitoring.domain;

import com.classmate.feedback.domain.FeedbackType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "session_metrics",
		uniqueConstraints = @UniqueConstraint(name = "uk_session_metrics_session", columnNames = "session_id")
)
public class SessionMetric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(nullable = false)
	private long questionCount;

	@Column(nullable = false)
	private long waitingQuestionCount;

	@Column(nullable = false)
	private long answeredQuestionCount;

	@Column(nullable = false)
	private long feedbackCount;

	@Column(nullable = false)
	private long confusedCount;

	@Column(nullable = false)
	private long fastPaceCount;

	@Column(nullable = false)
	private long needExampleCount;

	@Column(nullable = false)
	private double confusionScore;

	private LocalDateTime lastEventAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	protected SessionMetric() {
	}

	private SessionMetric(Long sessionId, Long lectureId) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
	}

	public static SessionMetric initialize(Long sessionId, Long lectureId) {
		return new SessionMetric(sessionId, lectureId);
	}

	public void increaseQuestionCount(LocalDateTime eventTime) {
		this.questionCount++;
		this.waitingQuestionCount++;
		updateEventTime(eventTime);
		recalculateConfusionScore();
	}

	public void increaseFeedbackCount(FeedbackType feedbackType, LocalDateTime eventTime) {
		this.feedbackCount++;
		if (feedbackType == FeedbackType.CONFUSED) {
			this.confusedCount++;
		}
		if (feedbackType == FeedbackType.FAST_PACE) {
			this.fastPaceCount++;
		}
		if (feedbackType == FeedbackType.NEED_EXAMPLE) {
			this.needExampleCount++;
		}
		updateEventTime(eventTime);
		recalculateConfusionScore();
	}

	private void updateEventTime(LocalDateTime eventTime) {
		this.lastEventAt = eventTime;
		this.updatedAt = LocalDateTime.now();
	}

	private void recalculateConfusionScore() {
		this.confusionScore = confusedCount
				+ fastPaceCount * 0.7
				+ needExampleCount * 0.5
				+ waitingQuestionCount * 0.8;
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = LocalDateTime.now();
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

	public long getQuestionCount() {
		return questionCount;
	}

	public long getWaitingQuestionCount() {
		return waitingQuestionCount;
	}

	public long getAnsweredQuestionCount() {
		return answeredQuestionCount;
	}

	public long getFeedbackCount() {
		return feedbackCount;
	}

	public long getConfusedCount() {
		return confusedCount;
	}

	public long getFastPaceCount() {
		return fastPaceCount;
	}

	public long getNeedExampleCount() {
		return needExampleCount;
	}

	public double getConfusionScore() {
		return confusionScore;
	}

	public LocalDateTime getLastEventAt() {
		return lastEventAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
