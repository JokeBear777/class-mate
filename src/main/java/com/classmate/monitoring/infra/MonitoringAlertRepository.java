package com.classmate.monitoring.infra;

import com.classmate.monitoring.domain.AlertType;
import com.classmate.monitoring.domain.MonitoringAlert;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoringAlertRepository extends JpaRepository<MonitoringAlert, Long> {

	List<MonitoringAlert> findBySessionIdOrderByCreatedAtDesc(Long sessionId);

	List<MonitoringAlert> findByLectureIdOrderByCreatedAtDesc(Long lectureId);

	boolean existsBySessionIdAndAlertTypeAndCreatedAtAfter(
			Long sessionId,
			AlertType alertType,
			LocalDateTime createdAt
	);
}
