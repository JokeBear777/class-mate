package com.classmate.lecture.infra;

import com.classmate.lecture.domain.LectureSession;
import com.classmate.lecture.domain.LectureSessionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureSessionRepository extends JpaRepository<LectureSession, Long> {

	List<LectureSession> findByLectureIdOrderByStartedAtDesc(Long lectureId);

	boolean existsByLectureIdAndStatus(Long lectureId, LectureSessionStatus status);
}
