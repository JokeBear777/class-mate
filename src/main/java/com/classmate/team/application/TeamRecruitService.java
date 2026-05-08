package com.classmate.team.application;

import com.classmate.auth.application.UserQueryService;
import com.classmate.common.exception.BusinessException;
import com.classmate.common.exception.ErrorCode;
import com.classmate.common.security.CurrentUserProvider;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.team.domain.TeamRecruit;
import com.classmate.team.domain.TeamRecruitStatus;
import com.classmate.team.dto.request.CreateTeamRecruitRequest;
import com.classmate.team.dto.request.UpdateTeamRecruitRequest;
import com.classmate.team.dto.response.TeamRecruitResponse;
import com.classmate.team.dto.response.TeamRecruitSummaryResponse;
import com.classmate.team.infra.TeamRecruitRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TeamRecruitService {

	private final TeamRecruitRepository teamRecruitRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final UserQueryService userQueryService;

	public TeamRecruitService(
			TeamRecruitRepository teamRecruitRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			UserQueryService userQueryService
	) {
		this.teamRecruitRepository = teamRecruitRepository;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.userQueryService = userQueryService;
	}

	@Transactional
	public TeamRecruitResponse createTeamRecruit(Long lectureId, CreateTeamRecruitRequest request) {
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(lectureId, currentUserId);
		validateMemberCount(request.requiredMembers(), request.currentMembers());

		TeamRecruit teamRecruit = teamRecruitRepository.save(TeamRecruit.create(
				lectureId,
				currentUserId,
				request.title().trim(),
				request.content().trim(),
				request.requiredMembers(),
				request.currentMembers()
		));

		return toResponse(teamRecruit);
	}

	public List<TeamRecruitSummaryResponse> getLectureTeamRecruits(Long lectureId, TeamRecruitStatus status) {
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(lectureId, currentUserId);

		List<TeamRecruit> teamRecruits = status == null
				? teamRecruitRepository.findByLectureIdAndDeletedFalseOrderByCreatedAtDesc(lectureId)
				: teamRecruitRepository.findByLectureIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(lectureId, status);

		return teamRecruits.stream()
				.map(this::toSummaryResponse)
				.toList();
	}

	public TeamRecruitResponse getTeamRecruit(Long teamRecruitId) {
		TeamRecruit teamRecruit = getTeamRecruitOrThrow(teamRecruitId);
		lectureAccessChecker.validateParticipant(teamRecruit.getLectureId(), currentUserId());
		return toResponse(teamRecruit);
	}

	@Transactional
	public TeamRecruitResponse updateTeamRecruit(Long teamRecruitId, UpdateTeamRecruitRequest request) {
		TeamRecruit teamRecruit = getTeamRecruitOrThrow(teamRecruitId);
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(teamRecruit.getLectureId(), currentUserId);
		validateAuthor(teamRecruit, currentUserId);
		validateMemberCount(request.requiredMembers(), request.currentMembers());

		teamRecruit.update(
				request.title().trim(),
				request.content().trim(),
				request.requiredMembers(),
				request.currentMembers()
		);
		return toResponse(teamRecruit);
	}

	@Transactional
	public TeamRecruitResponse closeTeamRecruit(Long teamRecruitId) {
		TeamRecruit teamRecruit = getTeamRecruitOrThrow(teamRecruitId);
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(teamRecruit.getLectureId(), currentUserId);
		validateAuthor(teamRecruit, currentUserId);
		teamRecruit.close();
		return toResponse(teamRecruit);
	}

	@Transactional
	public void deleteTeamRecruit(Long teamRecruitId) {
		TeamRecruit teamRecruit = getTeamRecruitOrThrow(teamRecruitId);
		Long currentUserId = currentUserId();
		lectureAccessChecker.validateParticipant(teamRecruit.getLectureId(), currentUserId);
		validateAuthorOrProfessorAssistant(teamRecruit, currentUserId);
		teamRecruit.delete();
	}

	private TeamRecruit getTeamRecruitOrThrow(Long teamRecruitId) {
		return teamRecruitRepository.findByIdAndDeletedFalse(teamRecruitId)
				.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_RECRUIT_NOT_FOUND));
	}

	private void validateAuthor(TeamRecruit teamRecruit, Long currentUserId) {
		if (!teamRecruit.getAuthorId().equals(currentUserId)) {
			throw new BusinessException(ErrorCode.TEAM_RECRUIT_ACCESS_DENIED);
		}
	}

	private void validateAuthorOrProfessorAssistant(TeamRecruit teamRecruit, Long currentUserId) {
		if (teamRecruit.getAuthorId().equals(currentUserId)) {
			return;
		}
		if (!lectureAccessChecker.isProfessorOrAssistant(teamRecruit.getLectureId(), currentUserId)) {
			throw new BusinessException(ErrorCode.TEAM_RECRUIT_ACCESS_DENIED);
		}
	}

	private void validateMemberCount(int requiredMembers, int currentMembers) {
		if (currentMembers > requiredMembers) {
			throw new BusinessException(ErrorCode.INVALID_TEAM_RECRUIT_MEMBER_COUNT);
		}
	}

	private TeamRecruitSummaryResponse toSummaryResponse(TeamRecruit teamRecruit) {
		return TeamRecruitSummaryResponse.from(teamRecruit, userQueryService.getUserName(teamRecruit.getAuthorId()));
	}

	private TeamRecruitResponse toResponse(TeamRecruit teamRecruit) {
		return TeamRecruitResponse.from(teamRecruit, userQueryService.getUserName(teamRecruit.getAuthorId()));
	}

	private Long currentUserId() {
		return currentUserProvider.getCurrentUserId();
	}
}
