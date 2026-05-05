package com.classmate.feedback.dto.request;

import com.classmate.feedback.domain.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Feedback submission request")
public record CreateFeedbackRequest(

		@Schema(description = "Feedback type", example = "CONFUSED")
		@NotNull(message = "Feedback type is required.")
		FeedbackType feedbackType
) {
}
