package com.classmate.team.api;

import com.classmate.common.response.ApiResponse;
import com.classmate.team.application.TeamRecruitService;
import com.classmate.team.domain.TeamRecruitStatus;
import com.classmate.team.dto.request.CreateTeamRecruitRequest;
import com.classmate.team.dto.request.UpdateTeamRecruitRequest;
import com.classmate.team.dto.response.TeamRecruitResponse;
import com.classmate.team.dto.response.TeamRecruitSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TeamRecruit", description = "Lecture team recruit APIs")
@RestController
@RequestMapping("/api/v1")
public class TeamRecruitController {

	private final TeamRecruitService teamRecruitService;

	public TeamRecruitController(TeamRecruitService teamRecruitService) {
		this.teamRecruitService = teamRecruitService;
	}

	@Operation(summary = "Create team recruit", description = "Creates a team recruit post for a lecture. Lecture participant access is required.")
	@PostMapping("/lectures/{lectureId}/team-recruits")
	public ApiResponse<TeamRecruitResponse> createTeamRecruit(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId,
			@Valid @RequestBody CreateTeamRecruitRequest request
	) {
		return ApiResponse.success("Team recruit created.", teamRecruitService.createTeamRecruit(lectureId, request));
	}

	@Operation(summary = "Get lecture team recruits", description = "Returns non-deleted team recruit posts for a lecture.")
	@GetMapping("/lectures/{lectureId}/team-recruits")
	public ApiResponse<List<TeamRecruitSummaryResponse>> getLectureTeamRecruits(
			@Parameter(description = "Lecture ID", example = "1")
			@PathVariable Long lectureId,
			@Parameter(description = "Status filter", example = "OPEN")
			@RequestParam(required = false) TeamRecruitStatus status
	) {
		return ApiResponse.success(teamRecruitService.getLectureTeamRecruits(lectureId, status));
	}

	@Operation(summary = "Get team recruit detail", description = "Returns a team recruit detail. Lecture participant access is required.")
	@GetMapping("/team-recruits/{teamRecruitId}")
	public ApiResponse<TeamRecruitResponse> getTeamRecruit(
			@Parameter(description = "Team recruit ID", example = "1")
			@PathVariable Long teamRecruitId
	) {
		return ApiResponse.success(teamRecruitService.getTeamRecruit(teamRecruitId));
	}

	@Operation(summary = "Update team recruit", description = "Updates a team recruit post. Author access is required.")
	@PatchMapping("/team-recruits/{teamRecruitId}")
	public ApiResponse<TeamRecruitResponse> updateTeamRecruit(
			@Parameter(description = "Team recruit ID", example = "1")
			@PathVariable Long teamRecruitId,
			@Valid @RequestBody UpdateTeamRecruitRequest request
	) {
		return ApiResponse.success("Team recruit updated.", teamRecruitService.updateTeamRecruit(teamRecruitId, request));
	}

	@Operation(summary = "Close team recruit", description = "Closes a team recruit post. Author access is required.")
	@PatchMapping("/team-recruits/{teamRecruitId}/close")
	public ApiResponse<TeamRecruitResponse> closeTeamRecruit(
			@Parameter(description = "Team recruit ID", example = "1")
			@PathVariable Long teamRecruitId
	) {
		return ApiResponse.success("Team recruit closed.", teamRecruitService.closeTeamRecruit(teamRecruitId));
	}

	@Operation(summary = "Delete team recruit", description = "Soft deletes a team recruit post. Author, professor, or assistant access is required.")
	@DeleteMapping("/team-recruits/{teamRecruitId}")
	public ApiResponse<Void> deleteTeamRecruit(
			@Parameter(description = "Team recruit ID", example = "1")
			@PathVariable Long teamRecruitId
	) {
		teamRecruitService.deleteTeamRecruit(teamRecruitId);
		return ApiResponse.success("Team recruit deleted.", null);
	}
}
