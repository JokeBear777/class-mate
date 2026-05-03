package com.classmate.lecture.application;

import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.lecture.domain.Lecture;
import com.classmate.lecture.domain.LectureEnrollment;
import com.classmate.lecture.domain.LectureRole;
import com.classmate.lecture.dto.request.CreateLectureRequest;
import com.classmate.lecture.dto.request.JoinLectureByCodeRequest;
import com.classmate.lecture.dto.response.LectureDetailResponse;
import com.classmate.lecture.dto.response.LectureJoinResponse;
import com.classmate.lecture.dto.response.LectureResponse;
import com.classmate.lecture.dto.response.LectureSummaryResponse;
import com.classmate.lecture.infra.LectureEnrollmentRepository;
import com.classmate.lecture.infra.LectureRepository;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class LectureService {

	private static final Long CURRENT_USER_ID = 1L;
	private static final String CURRENT_PROFESSOR_NAME = "Professor Kim";
	private static final String JOIN_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int JOIN_CODE_LENGTH = 6;

	private final LectureRepository lectureRepository;
	private final LectureEnrollmentRepository lectureEnrollmentRepository;
	private final SecureRandom secureRandom = new SecureRandom();

	public LectureService(
			LectureRepository lectureRepository,
			LectureEnrollmentRepository lectureEnrollmentRepository
	) {
		this.lectureRepository = lectureRepository;
		this.lectureEnrollmentRepository = lectureEnrollmentRepository;
	}

	@Transactional
	public LectureResponse createLecture(CreateLectureRequest request) {
		Long currentUserId = currentUserId();
		String joinCode = generateUniqueJoinCode();
		Lecture lecture = lectureRepository.save(
				Lecture.create(request.title(), request.description(), request.semester(), currentUserId, joinCode)
		);

		lectureEnrollmentRepository.save(LectureEnrollment.createProfessor(lecture.getId(), currentUserId));

		return LectureResponse.from(lecture, professorName(lecture));
	}

	public List<LectureSummaryResponse> getMyLectures() {
		Long currentUserId = currentUserId();
		List<Long> lectureIds = lectureEnrollmentRepository.findByUserId(currentUserId)
				.stream()
				.map(LectureEnrollment::getLectureId)
				.toList();

		return lectureRepository.findAllById(lectureIds)
				.stream()
				.map(lecture -> LectureSummaryResponse.from(lecture, professorName(lecture), true))
				.toList();
	}

	public List<LectureSummaryResponse> searchLectures(String keyword, String semester) {
		List<Lecture> lectures = findLectures(keyword, semester);
		Set<Long> enrolledLectureIds = getEnrolledLectureIds(currentUserId());

		return lectures.stream()
				.map(lecture -> LectureSummaryResponse.from(
						lecture,
						professorName(lecture),
						enrolledLectureIds.contains(lecture.getId())
				))
				.toList();
	}

	public LectureDetailResponse getLectureDetail(Long lectureId) {
		Lecture lecture = getLecture(lectureId);
		return lectureEnrollmentRepository.findByLectureIdAndUserId(lectureId, currentUserId())
				.map(enrollment -> LectureDetailResponse.from(lecture, professorName(lecture), true, enrollment.getRole().name()))
				.orElseGet(() -> LectureDetailResponse.from(lecture, professorName(lecture), false, "NONE"));
	}

	@Transactional
	public LectureJoinResponse joinLecture(Long lectureId, JoinLectureByCodeRequest request) {
		Lecture lecture = getLecture(lectureId);
		if (!lecture.getJoinCode().equals(normalizeJoinCode(request.joinCode()))) {
			throw new BusinessException(ErrorCode.INVALID_JOIN_CODE);
		}
		return join(lecture);
	}

	@Transactional
	public LectureJoinResponse joinLectureByCode(JoinLectureByCodeRequest request) {
		Lecture lecture = lectureRepository.findByJoinCode(normalizeJoinCode(request.joinCode()))
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_JOIN_CODE));
		return join(lecture);
	}

	private LectureJoinResponse join(Lecture lecture) {
		Long currentUserId = currentUserId();
		if (lectureEnrollmentRepository.existsByLectureIdAndUserId(lecture.getId(), currentUserId)) {
			throw new BusinessException(ErrorCode.LECTURE_ALREADY_JOINED);
		}

		lectureEnrollmentRepository.save(LectureEnrollment.createStudent(lecture.getId(), currentUserId));
		return LectureJoinResponse.from(lecture, professorName(lecture), LectureRole.STUDENT);
	}

	private Lecture getLecture(Long lectureId) {
		return lectureRepository.findById(lectureId)
				.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));
	}

	private List<Lecture> findLectures(String keyword, String semester) {
		boolean hasKeyword = StringUtils.hasText(keyword);
		boolean hasSemester = StringUtils.hasText(semester);

		if (hasKeyword && hasSemester) {
			return lectureRepository.findByTitleContainingIgnoreCaseAndSemester(keyword.trim(), semester.trim());
		}
		if (hasKeyword) {
			return lectureRepository.findByTitleContainingIgnoreCase(keyword.trim());
		}
		if (hasSemester) {
			return lectureRepository.findBySemester(semester.trim());
		}
		return lectureRepository.findAll();
	}

	private Set<Long> getEnrolledLectureIds(Long currentUserId) {
		List<Long> lectureIds = lectureEnrollmentRepository.findByUserId(currentUserId)
				.stream()
				.map(LectureEnrollment::getLectureId)
				.toList();
		return new HashSet<>(lectureIds);
	}

	private String generateUniqueJoinCode() {
		String joinCode;
		do {
			joinCode = generateJoinCode();
		} while (lectureRepository.existsByJoinCode(joinCode));
		return joinCode;
	}

	private String generateJoinCode() {
		StringBuilder builder = new StringBuilder(JOIN_CODE_LENGTH);
		for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
			builder.append(JOIN_CODE_CHARS.charAt(secureRandom.nextInt(JOIN_CODE_CHARS.length())));
		}
		return builder.toString();
	}

	private String normalizeJoinCode(String joinCode) {
		return joinCode.trim().toUpperCase(Locale.ROOT);
	}

	private Long currentUserId() {
		return CURRENT_USER_ID;
	}

	private String professorName(Lecture lecture) {
		return CURRENT_PROFESSOR_NAME;
	}
}
