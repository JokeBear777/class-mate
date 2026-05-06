package com.classmate.event.publisher;

import com.classmate.event.domain.ClassMateEventPayload;
import com.classmate.event.domain.ClassMateEventType;
import com.classmate.event.dto.FeedbackSubmittedEvent;
import com.classmate.event.dto.QuestionCreatedEvent;
import com.classmate.event.support.EventSerializationUtils;
import com.classmate.event.support.RedisStreamNames;
import com.classmate.feedback.domain.FeedbackEvent;
import com.classmate.question.domain.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStreamEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(RedisStreamEventPublisher.class);

	private final StringRedisTemplate redisTemplate;
	private final EventSerializationUtils eventSerializationUtils;

	public RedisStreamEventPublisher(
			StringRedisTemplate redisTemplate,
			EventSerializationUtils eventSerializationUtils
	) {
		this.redisTemplate = redisTemplate;
		this.eventSerializationUtils = eventSerializationUtils;
	}

	public void publishQuestionCreated(Question question) {
		QuestionCreatedEvent event = QuestionCreatedEvent.from(question);
		String payloadJson = eventSerializationUtils.toJson(event);
		ClassMateEventPayload payload = ClassMateEventPayload.create(
				ClassMateEventType.QUESTION_CREATED,
				"QUESTION",
				event.questionId(),
				event.lectureId(),
				event.sessionId(),
				payloadJson
		);
		publish(payload);
	}

	public void publishFeedbackSubmitted(FeedbackEvent feedbackEvent) {
		FeedbackSubmittedEvent event = FeedbackSubmittedEvent.from(feedbackEvent);
		String payloadJson = eventSerializationUtils.toJson(event);
		ClassMateEventPayload payload = ClassMateEventPayload.create(
				ClassMateEventType.FEEDBACK_SUBMITTED,
				"FEEDBACK",
				event.feedbackId(),
				event.lectureId(),
				event.sessionId(),
				payloadJson
		);
		publish(payload);
	}

	private void publish(ClassMateEventPayload payload) {
		try {
			redisTemplate.opsForStream().add(RedisStreamNames.LECTURE_EVENTS_STREAM, payload.toRecord());
		} catch (RuntimeException exception) {
			log.warn("Failed to publish Redis Stream event. eventType={}, aggregateId={}",
					payload.eventType(), payload.aggregateId(), exception);
			// TODO: introduce the Outbox pattern so events are reliably published after DB commit.
		}
	}
}
