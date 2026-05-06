package com.classmate.monitoring.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.monitoring.application.MonitoringService;
import com.classmate.monitoring.dto.response.LectureDashboardResponse;
import com.classmate.monitoring.dto.response.MonitoringAlertResponse;
import com.classmate.monitoring.dto.response.SessionDashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Monitoring", description = "Lecture session metric and anomaly monitoring APIs")
@RestController
@RequestMapping("/api/v1")
public class MonitoringController {

	private final MonitoringService monitoringService;

	public MonitoringController(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	@Operation(summary = "Get session dashboard", description = "Returns metrics and alerts for a session. Professor or assistant access is required.")
	@GetMapping("/sessions/{sessionId}/dashboard")
	public ApiResponse<SessionDashboardResponse> getSessionDashboard(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(monitoringService.getSessionDashboard(sessionId));
	}

	@Operation(summary = "Get lecture dashboard", description = "Returns recent metrics and alerts for a lecture. Professor or assistant access is required.")
	@GetMapping("/lectures/{lectureId}/dashboard")
	public ApiResponse<LectureDashboardResponse> getLectureDashboard(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId
	) {
		return ApiResponse.success(monitoringService.getLectureDashboard(lectureId));
	}

	@Operation(summary = "Get session alerts", description = "Returns monitoring alerts for a session. Professor or assistant access is required.")
	@GetMapping("/sessions/{sessionId}/alerts")
	public ApiResponse<List<MonitoringAlertResponse>> getSessionAlerts(
			@Parameter(description = "Session ID", example = "1")
			@PathVariable Long sessionId
	) {
		return ApiResponse.success(monitoringService.getSessionAlerts(sessionId));
	}
}
