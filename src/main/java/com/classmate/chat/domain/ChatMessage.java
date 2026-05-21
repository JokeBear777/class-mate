package com.classmate.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "chat_messages",
		indexes = {
				@Index(name = "idx_chat_messages_session_room_seq", columnList = "session_id, room_seq")
		},
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_chat_messages_session_room_seq", columnNames = {"session_id", "room_seq"})
		}
)
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

	@Column(name = "room_seq", nullable = false)
	private Long roomSeq;

	@Column(nullable = false, length = 500)
	private String content;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected ChatMessage() {
	}

	private ChatMessage(Long sessionId, Long lectureId, Long senderId, String senderName, Long roomSeq, String content) {
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.roomSeq = roomSeq;
		this.content = content;
	}

	public static ChatMessage create(
			Long sessionId,
			Long lectureId,
			Long senderId,
			String senderName,
			Long roomSeq,
			String content
	) {
		return new ChatMessage(sessionId, lectureId, senderId, senderName, roomSeq, content);
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

	public Long getRoomSeq() {
		return roomSeq;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
