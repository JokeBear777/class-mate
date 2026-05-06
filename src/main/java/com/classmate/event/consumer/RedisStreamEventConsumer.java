package com.classmate.event.consumer;

import com.classmate.event.domain.ClassMateEventPayload;
import com.classmate.event.publisher.RedisStreamDlqPublisher;
import com.classmate.event.support.RedisStreamNames;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
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
public class RedisStreamEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(RedisStreamEventConsumer.class);
	private static final String CONSUMER_NAME = consumerName();

	private final StringRedisTemplate redisTemplate;
	private final RealtimeEventConsumer realtimeEventConsumer;
	private final RedisStreamDlqPublisher redisStreamDlqPublisher;

	public RedisStreamEventConsumer(
			StringRedisTemplate redisTemplate,
			RealtimeEventConsumer realtimeEventConsumer,
			RedisStreamDlqPublisher redisStreamDlqPublisher
	) {
		this.redisTemplate = redisTemplate;
		this.realtimeEventConsumer = realtimeEventConsumer;
		this.redisStreamDlqPublisher = redisStreamDlqPublisher;
	}

	@Scheduled(fixedDelay = 1000)
	public void consume() {
		List<MapRecord<String, Object, Object>> records;
		try {
			records = redisTemplate.opsForStream().read(
					Consumer.from(RedisStreamNames.REALTIME_CONSUMER_GROUP, CONSUMER_NAME),
					StreamReadOptions.empty().count(20).block(Duration.ofMillis(500)),
					StreamOffset.create(RedisStreamNames.LECTURE_EVENTS_STREAM, ReadOffset.lastConsumed())
			);
		} catch (RuntimeException exception) {
			log.warn("Failed to read Redis Stream events.", exception);
			return;
		}

		if (records == null || records.isEmpty()) {
			return;
		}

		for (MapRecord<String, Object, Object> record : records) {
			consume(record);
		}
	}

	private void consume(MapRecord<String, Object, Object> record) {
		ClassMateEventPayload event = null;
		try {
			event = ClassMateEventPayload.fromRecord(record.getValue());
			realtimeEventConsumer.consume(event);
			redisTemplate.opsForStream().acknowledge(
					RedisStreamNames.LECTURE_EVENTS_STREAM,
					RedisStreamNames.REALTIME_CONSUMER_GROUP,
					record.getId()
			);
		} catch (RuntimeException exception) {
			log.warn("Failed to consume Redis Stream event. stream={}, recordId={}",
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
			redisTemplate.opsForStream().acknowledge(
					RedisStreamNames.LECTURE_EVENTS_STREAM,
					RedisStreamNames.REALTIME_CONSUMER_GROUP,
					record.getId()
			);
		}
	}

	private static String consumerName() {
		try {
			return InetAddress.getLocalHost().getHostName() + "-" + UUID.randomUUID();
		} catch (UnknownHostException exception) {
			return "classmate-" + UUID.randomUUID();
		}
	}
}
