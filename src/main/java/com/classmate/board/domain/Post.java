package com.classmate.board.domain;

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
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "author_id", nullable = false)
	private Long authorId;

	@Enumerated(EnumType.STRING)
	@Column(name = "post_type", nullable = false, length = 30)
	private PostType postType;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 3000)
	private String content;

	@Column(nullable = false)
	private boolean deleted;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	protected Post() {
	}

	private Post(Long lectureId, Long authorId, PostType postType, String title, String content) {
		this.lectureId = lectureId;
		this.authorId = authorId;
		this.postType = postType;
		this.title = title;
		this.content = content;
		this.deleted = false;
	}

	public static Post create(Long lectureId, Long authorId, PostType postType, String title, String content) {
		return new Post(lectureId, authorId, postType, title, content);
	}

	public void update(PostType postType, String title, String content) {
		this.postType = postType;
		this.title = title;
		this.content = content;
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
		this.updatedAt = now;
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

	public PostType getPostType() {
		return postType;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
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
