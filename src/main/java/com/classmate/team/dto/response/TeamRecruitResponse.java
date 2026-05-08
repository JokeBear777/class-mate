package com.classmate.team.dto.response;

import com.classmate.team.domain.TeamRecruit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Team recruit detail response")
public record TeamRecruitResponse(
		@Schema(description = "Team recruit ID", example = "1")
		Long teamRecruitId,
		@Schema(description = "Lecture ID", example = "1")
		Long lectureId,
		@Schema(description = "Author user ID", example = "3")
		Long authorId,
		@Schema(description = "Author name", example = "Kim Minwoo")
		String authorName,
		@Schema(description = "Recruit title", example = "Looking for two backend teammates")
		String title,
		@Schema(description = "Recruit content", example = "We are building the project API and need backend teammates.")
		String content,
		@Schema(description = "Required member count", example = "4")
		int requiredMembers,
		@Schema(description = "Current member count", example = "2")
		int currentMembers,
		@Schema(description = "Recruit status", example = "OPEN")
		String status,
		@Schema(description = "Created time")
		LocalDateTime createdAt,
		@Schema(description = "Updated time")
		LocalDateTime updatedAt,
		@Schema(description = "Closed time")
		LocalDateTime closedAt
) {

	public static TeamRecruitResponse from(TeamRecruit teamRecruit, String authorName) {
		return new TeamRecruitResponse(
				teamRecruit.getId(),
				teamRecruit.getLectureId(),
				teamRecruit.getAuthorId(),
				authorName,
				teamRecruit.getTitle(),
				teamRecruit.getContent(),
				teamRecruit.getRequiredMembers(),
				teamRecruit.getCurrentMembers(),
				teamRecruit.getStatus().name(),
				teamRecruit.getCreatedAt(),
				teamRecruit.getUpdatedAt(),
				teamRecruit.getClosedAt()
		);
	}
}
