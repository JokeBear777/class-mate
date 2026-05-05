package com.classmate.lecture.application;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.lecture.domain.LectureEnrollment;
import com.classmate.lecture.domain.LectureRole;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.lecture.domain.LectureSessionStatus;
import com.classmate.lecture.infra.LectureEnrollmentRepository;
import com.classmate.lecture.infra.LectureSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class LectureAccessChecker {

	private final LectureSessionRepository lectureSessionRepository;
	private final LectureEnrollmentRepository lectureEnrollmentRepository;

	public LectureAccessChecker(
			LectureSessionRepository lectureSessionRepository,
			LectureEnrollmentRepository lectureEnrollmentRepository
	) {
		this.lectureSessionRepository = lectureSessionRepository;
		this.lectureEnrollmentRepository = lectureEnrollmentRepository;
	}

	public LectureSession getSessionOrThrow(Long sessionId) {
		return lectureSessionRepository.findById(sessionId)
				.orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
	}

	public LectureEnrollment getEnrollmentOrThrow(Long lectureId, Long userId) {
		return lectureEnrollmentRepository.findByLectureIdAndUserId(lectureId, userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_ACCESS_DENIED));
	}

	public void validateParticipant(Long lectureId, Long userId) {
		getEnrollmentOrThrow(lectureId, userId);
	}

	public void validateProfessorOrAssistant(Long lectureId, Long userId) {
		LectureEnrollment enrollment = getEnrollmentOrThrow(lectureId, userId);
		if (enrollment.getRole() != LectureRole.PROFESSOR && enrollment.getRole() != LectureRole.ASSISTANT) {
			throw new BusinessException(ErrorCode.LECTURE_ACCESS_DENIED);
		}
	}

	public void validateActiveSession(LectureSession session) {
		if (session.getStatus() != LectureSessionStatus.ACTIVE) {
			throw new BusinessException(ErrorCode.SESSION_NOT_ACTIVE);
		}
	}
}
