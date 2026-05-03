package com.classmate.lecture.domain;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
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
@Table(name = "lecture_sessions")
public class LectureSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(nullable = false, length = 100)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private LectureSessionStatus status;

	@Column(nullable = false)
	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected LectureSession() {
	}

	private LectureSession(Long lectureId, String title, LectureSessionStatus status, LocalDateTime startedAt) {
		this.lectureId = lectureId;
		this.title = title;
		this.status = status;
		this.startedAt = startedAt;
	}

	public static LectureSession start(Long lectureId, String title) {
		return new LectureSession(lectureId, title, LectureSessionStatus.ACTIVE, LocalDateTime.now());
	}

	public void end() {
		if (status == LectureSessionStatus.ENDED) {
			throw new BusinessException(ErrorCode.SESSION_ALREADY_ENDED);
		}
		this.status = LectureSessionStatus.ENDED;
		this.endedAt = LocalDateTime.now();
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

	public String getTitle() {
		return title;
	}

	public LectureSessionStatus getStatus() {
		return status;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public LocalDateTime getEndedAt() {
		return endedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
