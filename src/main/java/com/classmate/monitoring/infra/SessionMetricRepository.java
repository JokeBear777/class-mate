package com.classmate.monitoring.infra;

import com.classmate.monitoring.domain.SessionMetric;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionMetricRepository extends JpaRepository<SessionMetric, Long> {

	Optional<SessionMetric> findBySessionId(Long sessionId);

	List<SessionMetric> findByLectureIdOrderByUpdatedAtDesc(Long lectureId);
}
