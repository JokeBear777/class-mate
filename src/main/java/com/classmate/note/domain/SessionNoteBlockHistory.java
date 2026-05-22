package com.classmate.note.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_note_block_histories")
public class SessionNoteBlockHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "note_id", nullable = false)
	private Long noteId;

	@Column(name = "block_id", nullable = false)
	private Long blockId;

	@Column(name = "session_id", nullable = false)
	private Long sessionId;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "previous_content", length = 5000)
	private String previousContent;

	@Column(name = "new_content", nullable = false, length = 5000)
	private String newContent;

	@Column(name = "previous_version", nullable = false)
	private long previousVersion;

	@Column(name = "new_version", nullable = false)
	private long newVersion;

	@Column(name = "edited_by", nullable = false)
	private Long editedBy;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected SessionNoteBlockHistory() {
	}

	private SessionNoteBlockHistory(
			Long noteId,
			Long blockId,
			Long sessionId,
			Long lectureId,
			String previousContent,
			String newContent,
			long previousVersion,
			long newVersion,
			Long editedBy
	) {
		this.noteId = noteId;
		this.blockId = blockId;
		this.sessionId = sessionId;
		this.lectureId = lectureId;
		this.previousContent = previousContent;
		this.newContent = newContent;
		this.previousVersion = previousVersion;
		this.newVersion = newVersion;
		this.editedBy = editedBy;
	}

	public static SessionNoteBlockHistory recordUpdate(
			SessionNoteBlock block,
			String previousContent,
			long previousVersion,
			Long editedBy
	) {
		return new SessionNoteBlockHistory(
				block.getNoteId(),
				block.getId(),
				block.getSessionId(),
				block.getLectureId(),
				previousContent,
				block.getContent(),
				previousVersion,
				block.getVersion(),
				editedBy
		);
	}

	@PrePersist
	void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getNoteId() {
		return noteId;
	}

	public Long getBlockId() {
		return blockId;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public String getPreviousContent() {
		return previousContent;
	}

	public String getNewContent() {
		return newContent;
	}

	public long getPreviousVersion() {
		return previousVersion;
	}

	public long getNewVersion() {
		return newVersion;
	}

	public Long getEditedBy() {
		return editedBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
