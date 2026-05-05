package com.classmate.feedback.domain;

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
@Table(name = "feedback_events")
public class FeedbackEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "feedback_type", nullable = false, length = 30)
	private FeedbackType feedbackType;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected FeedbackEvent() {
	}

	private FeedbackEvent(Long sessionId, Long lectureId, Long userId, FeedbackType feedbackType) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.userId = userId;
		this.feedbackType = feedbackType;
	}

	public static FeedbackEvent create(Long sessionId, Long lectureId, Long userId, FeedbackType feedbackType) {
		return new FeedbackEvent(sessionId, lectureId, userId, feedbackType);
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

	public Long getUserId() {
		return userId;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
