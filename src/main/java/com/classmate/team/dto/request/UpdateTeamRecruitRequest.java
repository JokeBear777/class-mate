package com.classmate.team.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Team recruit update request")
public record UpdateTeamRecruitRequest(

		@Schema(description = "Recruit title", example = "Looking for one backend teammate")
		@NotBlank(message = "Title is required.")
		@Size(max = 100, message = "Title must be 100 characters or fewer.")
		String title,

		@Schema(description = "Recruit content", example = "One backend role is still open.")
		@NotBlank(message = "Content is required.")
		@Size(max = 3000, message = "Content must be 3000 characters or fewer.")
		String content,

		@Schema(description = "Required member count", example = "4")
		@Min(value = 1, message = "Required members must be at least 1.")
		@Max(value = 20, message = "Required members must be 20 or fewer.")
		int requiredMembers,

		@Schema(description = "Current member count", example = "3")
		@Min(value = 1, message = "Current members must be at least 1.")
		@Max(value = 20, message = "Current members must be 20 or fewer.")
		int currentMembers
) {
}
