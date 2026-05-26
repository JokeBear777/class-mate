package com.classmate.note.domain;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "session_note_blocks",
		indexes = {
				@Index(name = "idx_session_note_blocks_note_order", columnList = "note_id, block_order"),
				@Index(name = "idx_session_note_blocks_session", columnList = "session_id")
		}
)
public class SessionNoteBlock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "note_id", nullable = false)
	private Long noteId;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "block_order", nullable = false)
	private int blockOrder;

	@Enumerated(EnumType.STRING)
	@Column(name = "block_type", nullable = false, length = 20)
	private SessionNoteBlockType blockType;

	@Column(nullable = false, length = 5000)
	private String content;

	@Column(nullable = false)
	private long version;

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(nullable = false)
	private boolean deleted;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	protected SessionNoteBlock() {
	}

	private SessionNoteBlock(
			Long noteId,
			Long sessionId,
			Long lectureId,
			int blockOrder,
			SessionNoteBlockType blockType,
			String content,
			Long createdBy
	) {
		this.noteId = noteId;
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.blockOrder = blockOrder;
		this.blockType = blockType;
		this.content = content;
		this.version = 1L;
		this.createdBy = createdBy;
		this.updatedBy = createdBy;
		this.deleted = false;
	}

	public static SessionNoteBlock create(
			Long noteId,
			Long sessionId,
			Long lectureId,
			int blockOrder,
			SessionNoteBlockType blockType,
			String content,
			Long createdBy
	) {
		return new SessionNoteBlock(noteId, sessionId, lectureId, blockOrder, blockType, content, createdBy);
	}

	public void update(String newContent, long requestVersion, Long userId) {
		validateNotDeleted();
		validateVersion(requestVersion);
		this.content = newContent;
		this.version++;
		this.updatedBy = userId;
		this.updatedAt = LocalDateTime.now();
	}

	public void delete(Long userId) {
		validateNotDeleted();
		this.deleted = true;
		this.updatedBy = userId;
		this.updatedAt = LocalDateTime.now();
		this.deletedAt = LocalDateTime.now();
	}

	public void validateVersion(long requestVersion) {
		if (this.version != requestVersion) {
			throw new BusinessException(ErrorCode.SESSION_NOTE_BLOCK_VERSION_CONFLICT);
		}
	}

	private void validateNotDeleted() {
		if (deleted) {
			throw new BusinessException(ErrorCode.SESSION_NOTE_BLOCK_ALREADY_DELETED);
		}
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

	public Long getNoteId() {
		return noteId;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public int getBlockOrder() {
		return blockOrder;
	}

	public SessionNoteBlockType getBlockType() {
		return blockType;
	}

	public String getContent() {
		return content;
	}

	public long getVersion() {
		return version;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}
}
