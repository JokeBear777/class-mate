package com.classmate.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "author_id", nullable = false)
	private Long authorId;

	@Column(name = "anonymous_key", nullable = false, length = 100)
	private String anonymousKey;

	@Column(nullable = false, length = 1000)
	private String content;

	@Column(length = 1000)
	private String answer;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private QuestionStatus status;

	@Column(nullable = false)
	private boolean pinned;

	@Column(nullable = false)
	private boolean hidden;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime answeredAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	protected Question() {
	}

	private Question(Long sessionId, Long lectureId, Long authorId, String anonymousKey, String content) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.authorId = authorId;
		this.anonymousKey = anonymousKey;
		this.content = content;
		this.status = QuestionStatus.WAITING;
		this.pinned = false;
		this.hidden = false;
	}

	public static Question create(
			Long sessionId,
			Long lectureId,
			Long authorId,
			String anonymousKey,
			String content
	) {
		return new Question(sessionId, lectureId, authorId, anonymousKey, content);
	}

	public void answer(String answer) {
		this.answer = answer;
		this.status = QuestionStatus.ANSWERED;
		this.answeredAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public void changePinned(boolean pinned) {
		this.pinned = pinned;
		this.updatedAt = LocalDateTime.now();
	}

	public void changeHidden(boolean hidden) {
		this.hidden = hidden;
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

	public Long getAuthorId() {
		return authorId;
	}

	public String getAnonymousKey() {
		return anonymousKey;
	}

	public String getContent() {
		return content;
	}

	public String getAnswer() {
		return answer;
	}

	public QuestionStatus getStatus() {
		return status;
	}

	public boolean isPinned() {
		return pinned;
	}

	public boolean isHidden() {
		return hidden;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getAnsweredAt() {
		return answeredAt;
	}
}
