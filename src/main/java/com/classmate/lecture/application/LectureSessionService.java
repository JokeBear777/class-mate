package com.classmate.lecture.application;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.lecture.domain.Lecture;
import com.classmate.lecture.domain.LectureEnrollment;
import com.classmate.lecture.domain.LectureRole;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.lecture.domain.LectureSessionStatus;
import com.classmate.lecture.dto.request.StartLectureSessionRequest;
import com.classmate.lecture.dto.response.LectureSessionResponse;
import com.classmate.lecture.infra.LectureEnrollmentRepository;
import com.classmate.lecture.infra.LectureRepository;
import com.classmate.lecture.infra.LectureSessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class LectureSessionService {

	private static final Long CURRENT_USER_ID = 1L;
	private static final String DEFAULT_SESSION_TITLE = "Live lecture session";

	private final LectureRepository lectureRepository;
	private final LectureEnrollmentRepository lectureEnrollmentRepository;
	private final LectureSessionRepository lectureSessionRepository;

	public LectureSessionService(
			LectureRepository lectureRepository,
			LectureEnrollmentRepository lectureEnrollmentRepository,
			LectureSessionRepository lectureSessionRepository
	) {
		this.lectureRepository = lectureRepository;
		this.lectureEnrollmentRepository = lectureEnrollmentRepository;
		this.lectureSessionRepository = lectureSessionRepository;
	}

	@Transactional
	public LectureSessionResponse startSession(Long lectureId, StartLectureSessionRequest request) {
		Lecture lecture = getLecture(lectureId);
		validateProfessorAccess(lecture.getId());

		if (lectureSessionRepository.existsByLectureIdAndStatus(lecture.getId(), LectureSessionStatus.ACTIVE)) {
			throw new BusinessException(ErrorCode.LECTURE_SESSION_ALREADY_ACTIVE);
		}

		String title = StringUtils.hasText(request.title()) ? request.title().trim() : DEFAULT_SESSION_TITLE;
		LectureSession session = lectureSessionRepository.save(LectureSession.start(lecture.getId(), title));
		return LectureSessionResponse.from(session);
	}

	public List<LectureSessionResponse> getLectureSessions(Long lectureId) {
		Lecture lecture = getLecture(lectureId);
		validateEnrolled(lecture.getId());

		return lectureSessionRepository.findByLectureIdOrderByStartedAtDesc(lecture.getId())
				.stream()
				.map(LectureSessionResponse::from)
				.toList();
	}

	@Transactional
	public LectureSessionResponse endSession(Long sessionId) {
		LectureSession session = lectureSessionRepository.findById(sessionId)
				.orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
		getLecture(session.getLectureId());
		validateProfessorAccess(session.getLectureId());

		session.end();
		return LectureSessionResponse.from(session);
	}

	private Lecture getLecture(Long lectureId) {
		return lectureRepository.findById(lectureId)
				.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));
	}

	private void validateEnrolled(Long lectureId) {
		if (!lectureEnrollmentRepository.existsByLectureIdAndUserId(lectureId, currentUserId())) {
			throw new BusinessException(ErrorCode.LECTURE_ACCESS_DENIED);
		}
	}

	private void validateProfessorAccess(Long lectureId) {
		LectureEnrollment enrollment = lectureEnrollmentRepository.findByLectureIdAndUserId(lectureId, currentUserId())
				.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_ACCESS_DENIED));

		if (enrollment.getRole() != LectureRole.PROFESSOR && enrollment.getRole() != LectureRole.ASSISTANT) {
			throw new BusinessException(ErrorCode.LECTURE_ACCESS_DENIED);
		}
	}

	private Long currentUserId() {
		return CURRENT_USER_ID;
	}
}
