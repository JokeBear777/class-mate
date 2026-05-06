package com.classmate.monitoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Session dashboard response")
public record SessionDashboardResponse(
		@Schema(description = "Session ID", example = "1")
		Long sessionId,
		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,
		@Schema(description = "Session metric")
		SessionMetricResponse metric,
		@Schema(description = "Recent monitoring alerts")
		List<MonitoringAlertResponse> alerts
) {
}
