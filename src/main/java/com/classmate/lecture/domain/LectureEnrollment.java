package com.classmate.lecture.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "lecture_enrollments",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_lecture_enrollments_lecture_user",
				columnNames = {"lecture_id", "user_id"}
		)
)
public class LectureEnrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private LectureRole role;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	protected LectureEnrollment() {
	}

	private LectureEnrollment(Long lectureId, Long userId, LectureRole role) {
		this.lectureId = lectureId;
		this.userId = userId;
		this.role = role;
	}

	public static LectureEnrollment createProfessor(Long lectureId, Long professorId) {
		return new LectureEnrollment(lectureId, professorId, LectureRole.PROFESSOR);
	}

	public static LectureEnrollment createStudent(Long lectureId, Long studentId) {
		return new LectureEnrollment(lectureId, studentId, LectureRole.STUDENT);
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

	public Long getUserId() {
		return userId;
	}

	public LectureRole getRole() {
		return role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
