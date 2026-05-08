package com.classmate.team.domain;

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
@Table(name = "team_recruits")
public class TeamRecruit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "author_id", nullable = false)
	private Long authorId;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 3000)
	private String content;

	@Column(nullable = false)
	private int requiredMembers;

	@Column(nullable = false)
	private int currentMembers;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TeamRecruitStatus status;

	@Column(nullable = false)
	private boolean deleted;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime closedAt;

	private LocalDateTime deletedAt;

	protected TeamRecruit() {
	}

	private TeamRecruit(
			Long lectureId,
			Long authorId,
			String title,
			String content,
			int requiredMembers,
			int currentMembers
	) {
		this.lectureId = lectureId;
		this.authorId = authorId;
		this.title = title;
		this.content = content;
		this.requiredMembers = requiredMembers;
		this.currentMembers = currentMembers;
		this.status = TeamRecruitStatus.OPEN;
		this.deleted = false;
		if (currentMembers >= requiredMembers) {
			close();
		}
	}

	public static TeamRecruit create(
			Long lectureId,
			Long authorId,
			String title,
			String content,
			int requiredMembers,
			int currentMembers
	) {
		return new TeamRecruit(lectureId, authorId, title, content, requiredMembers, currentMembers);
	}

	public void update(String title, String content, int requiredMembers, int currentMembers) {
		this.title = title;
		this.content = content;
		this.requiredMembers = requiredMembers;
		this.currentMembers = currentMembers;
		this.updatedAt = LocalDateTime.now();

		if (this.status == TeamRecruitStatus.CLOSED) {
			return;
		}
		if (currentMembers >= requiredMembers) {
			close();
		}
	}

	public void close() {
		if (this.status == TeamRecruitStatus.CLOSED) {
			return;
		}
		this.status = TeamRecruitStatus.CLOSED;
		this.closedAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public void delete() {
		this.deleted = true;
		this.deletedAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		if (this.updatedAt == null) {
			this.updatedAt = now;
		}
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public int getRequiredMembers() {
		return requiredMembers;
	}

	public int getCurrentMembers() {
		return currentMembers;
	}

	public TeamRecruitStatus getStatus() {
		return status;
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

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}
}
