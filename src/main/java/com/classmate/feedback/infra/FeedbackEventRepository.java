package com.classmate.feedback.infra;

import com.classmate.feedback.domain.FeedbackEvent;
import com.classmate.feedback.domain.FeedbackType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackEventRepository extends JpaRepository<FeedbackEvent, Long> {

	List<FeedbackEvent> findBySessionId(Long sessionId);

	long countBySessionIdAndFeedbackType(Long sessionId, FeedbackType feedbackType);

	long countBySessionId(Long sessionId);
}
