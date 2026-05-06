package com.classmate.monitoring.dto.response;

import com.classmate.monitoring.domain.SessionMetric;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Session metric response")
public record SessionMetricResponse(
		@Schema(description = "Session ID", example = "1")
		Long sessionId,
		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,
		@Schema(description = "Question count", example = "12")
		long questionCount,
		@Schema(description = "Waiting question count", example = "8")
		long waitingQuestionCount,
		@Schema(description = "Answered question count", example = "4")
		long answeredQuestionCount,
		@Schema(description = "Feedback count", example = "15")
		long feedbackCount,
		@Schema(description = "CONFUSED feedback count", example = "5")
		long confusedCount,
		@Schema(description = "FAST_PACE feedback count", example = "3")
		long fastPaceCount,
		@Schema(description = "NEED_EXAMPLE feedback count", example = "2")
		long needExampleCount,
		@Schema(description = "Rule-based confusion score", example = "12.3")
		double confusionScore,
		@Schema(description = "Last event at")
		LocalDateTime lastEventAt,
		@Schema(description = "Updated at")
		LocalDateTime updatedAt
) {

	public static SessionMetricResponse from(SessionMetric metric) {
		return new SessionMetricResponse(
				metric.getSessionId(),
				metric.getLectureId(),
				metric.getQuestionCount(),
				metric.getWaitingQuestionCount(),
				metric.getAnsweredQuestionCount(),
				metric.getFeedbackCount(),
				metric.getConfusedCount(),
				metric.getFastPaceCount(),
				metric.getNeedExampleCount(),
				metric.getConfusionScore(),
				metric.getLastEventAt(),
				metric.getUpdatedAt()
		);
	}

	public static SessionMetricResponse empty(Long sessionId, Long lectureId) {
		return new SessionMetricResponse(sessionId, lectureId, 0, 0, 0, 0, 0, 0, 0, 0.0, null, null);
	}
}
