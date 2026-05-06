package com.classmate.event.consumer;

import com.classmate.event.domain.ClassMateEventPayload;
import com.classmate.event.dto.FeedbackSubmittedEvent;
import com.classmate.event.dto.QuestionCreatedEvent;
import com.classmate.event.support.EventSerializationUtils;
import com.classmate.realtime.application.RealtimeMessageService;
import com.classmate.realtime.dto.RealtimeFeedbackMessage;
import com.classmate.realtime.dto.RealtimeQuestionMessage;
import org.springframework.stereotype.Component;

@Component
public class RealtimeEventConsumer {

	private final EventSerializationUtils eventSerializationUtils;
	private final RealtimeMessageService realtimeMessageService;

	public RealtimeEventConsumer(
			EventSerializationUtils eventSerializationUtils,
			RealtimeMessageService realtimeMessageService
	) {
		this.eventSerializationUtils = eventSerializationUtils;
		this.realtimeMessageService = realtimeMessageService;
	}

	public void consume(ClassMateEventPayload event) {
		switch (event.eventType()) {
			case QUESTION_CREATED -> handleQuestionCreated(event);
			case FEEDBACK_SUBMITTED -> handleFeedbackSubmitted(event);
		}
	}

	private void handleQuestionCreated(ClassMateEventPayload event) {
		QuestionCreatedEvent payload = eventSerializationUtils.fromJson(
				event.payloadJson(),
				QuestionCreatedEvent.class
		);
		realtimeMessageService.sendQuestionCreated(event.sessionId(), RealtimeQuestionMessage.from(payload));
	}

	private void handleFeedbackSubmitted(ClassMateEventPayload event) {
		FeedbackSubmittedEvent payload = eventSerializationUtils.fromJson(
				event.payloadJson(),
				FeedbackSubmittedEvent.class
		);
		realtimeMessageService.sendFeedbackSubmitted(event.sessionId(), RealtimeFeedbackMessage.from(payload));
	}
}
