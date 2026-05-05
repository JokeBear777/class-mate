package com.classmate.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Feedback summary response")
public record FeedbackSummaryResponse(

		@Schema(description = "Session ID", example = "1")
		Long sessionId,

		@Schema(description = "Total feedback count", example = "12")
		long totalCount,

		@Schema(description = "FAST_PACE feedback count", example = "3")
		long fastPaceCount,

		@Schema(description = "CONFUSED feedback count", example = "7")
		long confusedCount,

		@Schema(description = "NEED_EXAMPLE feedback count", example = "2")
		long needExampleCount
) {
}
