package com.classmate.monitoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Lecture dashboard response")
public record LectureDashboardResponse(
		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,
		@Schema(description = "Recent session metrics")
		List<SessionMetricResponse> recentSessions,
		@Schema(description = "Recent monitoring alerts")
		List<MonitoringAlertResponse> recentAlerts
) {
}
