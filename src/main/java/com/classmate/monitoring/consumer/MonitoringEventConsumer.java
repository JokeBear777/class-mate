package com.classmate.monitoring.consumer;

import com.classmate.event.domain.ClassMateEventPayload;
import com.classmate.event.dto.FeedbackSubmittedEvent;
import com.classmate.event.dto.QuestionCreatedEvent;
import com.classmate.event.publisher.RedisStreamDlqPublisher;
import com.classmate.event.support.EventSerializationUtils;
import com.classmate.event.support.RedisStreamNames;
import com.classmate.monitoring.application.MonitoringService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitoringEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(MonitoringEventConsumer.class);
	private static final String CONSUMER_NAME = consumerName();

	private final StringRedisTemplate redisTemplate;
	private final EventSerializationUtils eventSerializationUtils;
	private final MonitoringService monitoringService;
	private final RedisStreamDlqPublisher redisStreamDlqPublisher;

	public MonitoringEventConsumer(
			StringRedisTemplate redisTemplate,
			EventSerializationUtils eventSerializationUtils,
			MonitoringService monitoringService,
			RedisStreamDlqPublisher redisStreamDlqPublisher
	) {
		this.redisTemplate = redisTemplate;
		this.eventSerializationUtils = eventSerializationUtils;
		this.monitoringService = monitoringService;
		this.redisStreamDlqPublisher = redisStreamDlqPublisher;
	}

	@Scheduled(fixedDelay = 1000)
	public void consume() {
		List<MapRecord<String, Object, Object>> records;
		try {
			records = redisTemplate.opsForStream().read(
					Consumer.from(RedisStreamNames.MONITORING_CONSUMER_GROUP, CONSUMER_NAME),
					StreamReadOptions.empty().count(20).block(Duration.ofMillis(500)),
					StreamOffset.create(RedisStreamNames.LECTURE_EVENTS_STREAM, ReadOffset.lastConsumed())
			);
		} catch (RuntimeException exception) {
			log.warn("Failed to read monitoring Redis Stream events.", exception);
			return;
		}

		if (records == null || records.isEmpty()) {
			return;
		}

		for (MapRecord<String, Object, Object> record : records) {
			consume(record);
		}
	}

	public void handleQuestionCreated(QuestionCreatedEvent event) {
		monitoringService.handleQuestionCreated(event);
	}

	public void handleFeedbackSubmitted(FeedbackSubmittedEvent event) {
		monitoringService.handleFeedbackSubmitted(event);
	}

	private void consume(MapRecord<String, Object, Object> record) {
		ClassMateEventPayload event = null;
		try {
			event = ClassMateEventPayload.fromRecord(record.getValue());
			dispatch(event);
			ack(record);
		} catch (RuntimeException exception) {
			log.warn("Failed to consume monitoring Redis Stream event. stream={}, recordId={}",
					record.getStream(), record.getId(), exception);
			if (event != null) {
				redisStreamDlqPublisher.publish(
						RedisStreamNames.LECTURE_EVENTS_STREAM,
						record.getId().getValue(),
						event,
						exception
				);
			} else {
				redisStreamDlqPublisher.publishMalformed(
						RedisStreamNames.LECTURE_EVENTS_STREAM,
						record.getId().getValue(),
						record.getValue(),
						exception
				);
			}
			ack(record);
		}
	}

	private void dispatch(ClassMateEventPayload event) {
		switch (event.eventType()) {
			case QUESTION_CREATED -> handleQuestionCreated(eventSerializationUtils.fromJson(
					event.payloadJson(),
					QuestionCreatedEvent.class
			));
			case FEEDBACK_SUBMITTED -> handleFeedbackSubmitted(eventSerializationUtils.fromJson(
					event.payloadJson(),
					FeedbackSubmittedEvent.class
			));
		}
	}

	private void ack(MapRecord<String, Object, Object> record) {
		redisTemplate.opsForStream().acknowledge(
				RedisStreamNames.LECTURE_EVENTS_STREAM,
				RedisStreamNames.MONITORING_CONSUMER_GROUP,
				record.getId()
		);
	}

	private static String consumerName() {
		try {
			return InetAddress.getLocalHost().getHostName() + "-" + UUID.randomUUID();
		} catch (UnknownHostException exception) {
			return "classmate-monitoring-" + UUID.randomUUID();
		}
	}
}
