package com.classmate.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "sender_id", nullable = false)
	private Long senderId;

	@Column(name = "sender_name", nullable = false, length = 50)
	private String senderName;

	@Column(nullable = false, length = 500)
	private String content;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected ChatMessage() {
	}

	private ChatMessage(Long sessionId, Long lectureId, Long senderId, String senderName, String content) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.content = content;
	}

	public static ChatMessage create(
			Long sessionId,
			Long lectureId,
			Long senderId,
			String senderName,
			String content
	) {
		return new ChatMessage(sessionId, lectureId, senderId, senderName, content);
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

	public Long getSenderId() {
		return senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
