package com.classmate.note.domain;

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
		name = "session_shared_notes",
		uniqueConstraints = @UniqueConstraint(name = "uk_session_shared_notes_session", columnNames = "session_id")
)
public class SessionSharedNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false)
	private long documentRevision;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	protected SessionSharedNote() {
	}

	private SessionSharedNote(Long sessionId, Long lectureId, String title) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.title = title;
		this.documentRevision = 1L;
	}

	public static SessionSharedNote create(Long sessionId, Long lectureId, String title) {
		return new SessionSharedNote(sessionId, lectureId, title);
	}

	public void increaseRevision() {
		this.documentRevision++;
		this.updatedAt = LocalDateTime.now();
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

	public String getTitle() {
		return title;
	}

	public long getDocumentRevision() {
		return documentRevision;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
