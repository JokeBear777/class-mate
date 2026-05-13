package com.classmate.notification.infra;

import com.classmate.notification.domain.Notification;
import com.classmate.notification.domain.NotificationChannel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findBySessionIdOrderByCreatedAtDesc(Long sessionId);

	List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

	List<Notification> findByLectureIdOrderByCreatedAtDesc(Long lectureId);

	List<Notification> findBySessionIdAndReadAtIsNull(Long sessionId);

	boolean existsBySourceAlertIdAndChannel(Long sourceAlertId, NotificationChannel channel);
}
