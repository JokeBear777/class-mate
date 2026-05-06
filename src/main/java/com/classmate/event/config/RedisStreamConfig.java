package com.classmate.event.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.classmate.event.support.RedisStreamNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RedisStreamConfig {

	private static final Logger log = LoggerFactory.getLogger(RedisStreamConfig.class);

	private final StringRedisTemplate redisTemplate;

	public RedisStreamConfig(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@EventListener(ContextRefreshedEvent.class)
	public void createConsumerGroups() {
		createGroup(RedisStreamNames.LECTURE_EVENTS_STREAM, RedisStreamNames.REALTIME_CONSUMER_GROUP);
		createGroup(RedisStreamNames.LECTURE_EVENTS_STREAM, RedisStreamNames.MONITORING_CONSUMER_GROUP);
	}

	private void createGroup(String stream, String group) {
		try {
			redisTemplate.execute((RedisCallback<Void>) connection -> {
				connection.execute(
						"XGROUP",
						"CREATE".getBytes(UTF_8),
						stream.getBytes(UTF_8),
						group.getBytes(UTF_8),
						"0".getBytes(UTF_8),
						"MKSTREAM".getBytes(UTF_8)
				);
				return null;
			});
			log.info("Redis Stream consumer group created. stream={}, group={}", stream, group);
		} catch (DataAccessException exception) {
			if (exception.getMessage() != null && exception.getMessage().contains("BUSYGROUP")) {
				log.debug("Redis Stream consumer group already exists. stream={}, group={}", stream, group);
				return;
			}
			log.warn("Failed to create Redis Stream consumer group. stream={}, group={}", stream, group, exception);
		}
	}
}
