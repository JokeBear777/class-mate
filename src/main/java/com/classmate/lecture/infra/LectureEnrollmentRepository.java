package com.classmate.lecture.infra;

import com.classmate.lecture.domain.LectureEnrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureEnrollmentRepository extends JpaRepository<LectureEnrollment, Long> {

	boolean existsByLectureIdAndUserId(Long lectureId, Long userId);

	Optional<LectureEnrollment> findByLectureIdAndUserId(Long lectureId, Long userId);

	List<LectureEnrollment> findByUserId(Long userId);

	List<LectureEnrollment> findByLectureId(Long lectureId);
}
