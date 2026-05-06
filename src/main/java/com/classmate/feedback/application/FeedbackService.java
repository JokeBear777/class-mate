package com.classmate.feedback.application;

import com.classmate.common.security.CurrentUserProvider;
import com.classmate.feedback.domain.FeedbackEvent;
import com.classmate.feedback.domain.FeedbackType;
import com.classmate.feedback.dto.request.CreateFeedbackRequest;
import com.classmate.feedback.dto.response.FeedbackResponse;
import com.classmate.feedback.dto.response.FeedbackSummaryResponse;
import com.classmate.feedback.infra.FeedbackEventRepository;
import com.classmate.lecture.application.LectureAccessChecker;
import com.classmate.lecture.domain.LectureSession;
import com.classmate.realtime.application.RealtimeMessageService;
import com.classmate.realtime.dto.RealtimeFeedbackMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeedbackService {

	private final FeedbackEventRepository feedbackEventRepository;
	private final CurrentUserProvider currentUserProvider;
	private final LectureAccessChecker lectureAccessChecker;
	private final RealtimeMessageService realtimeMessageService;

	public FeedbackService(
			FeedbackEventRepository feedbackEventRepository,
			CurrentUserProvider currentUserProvider,
			LectureAccessChecker lectureAccessChecker,
			RealtimeMessageService realtimeMessageService
	) {
		this.feedbackEventRepository = feedbackEventRepository;
		this.currentUserProvider = currentUserProvider;
		this.lectureAccessChecker = lectureAccessChecker;
		this.realtimeMessageService = realtimeMessageService;
	}

	@Transactional
	public FeedbackResponse createFeedback(Long sessionId, CreateFeedbackRequest request) {
		Long currentUserId = currentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateActiveSession(session);
		lectureAccessChecker.validateParticipant(session.getLectureId(), currentUserId);

		FeedbackEvent feedbackEvent = feedbackEventRepository.save(FeedbackEvent.create(
				session.getId(),
				session.getLectureId(),
				currentUserId,
				request.feedbackType()
		));
		realtimeMessageService.sendFeedbackSubmitted(session.getId(), RealtimeFeedbackMessage.from(feedbackEvent));
		// TODO: replace direct WebSocket send with Redis Stream event publishing after event pipeline is implemented.

		return FeedbackResponse.from(feedbackEvent);
	}

	public FeedbackSummaryResponse getFeedbackSummary(Long sessionId) {
		Long currentUserId = currentUserId();
		LectureSession session = lectureAccessChecker.getSessionOrThrow(sessionId);
		lectureAccessChecker.validateProfessorOrAssistant(session.getLectureId(), currentUserId);

		return new FeedbackSummaryResponse(
				sessionId,
				feedbackEventRepository.countBySessionId(sessionId),
				feedbackEventRepository.countBySessionIdAndFeedbackType(sessionId, FeedbackType.FAST_PACE),
				feedbackEventRepository.countBySessionIdAndFeedbackType(sessionId, FeedbackType.CONFUSED),
				feedbackEventRepository.countBySessionIdAndFeedbackType(sessionId, FeedbackType.NEED_EXAMPLE)
		);
	}

	private Long currentUserId() {
		return currentUserProvider.getCurrentUserId();
	}
}
