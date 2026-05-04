package com.classmate.lecture.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "lectures")
public class Lecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(length = 500)
	private String description;

	@Column(nullable = false, length = 20)
	private String semester;

	@Column(nullable = false)
	private Long professorId;

	@Column(nullable = false, unique = true, length = 20)
	private String joinCode;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	protected Lecture() {
	}

	private Lecture(String title, String description, String semester, Long professorId, String joinCode) {
		this.title = title;
		this.description = description;
		this.semester = semester;
		this.professorId = professorId;
		this.joinCode = joinCode;
	}

	public static Lecture create(String title, String description, String semester, Long professorId, String joinCode) {
		return new Lecture(title, description, semester, professorId, joinCode);
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

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getSemester() {
		return semester;
	}

	public Long getProfessorId() {
		return professorId;
	}

	public String getJoinCode() {
		return joinCode;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
