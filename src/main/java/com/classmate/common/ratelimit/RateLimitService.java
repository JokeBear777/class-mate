package com.classmate.common.ratelimit;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

	private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

	private final StringRedisTemplate redisTemplate;

	public RateLimitService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RateLimitResult check(String key, RateLimitPolicy policy) {
		try {
			// TODO: Replace INCR + EXPIRE with Lua script for atomic rate limit operation.
			Long count = redisTemplate.opsForValue().increment(key);
			if (count == null) {
				return RateLimitResult.failOpen(key, policy);
			}
			if (count == 1) {
				redisTemplate.expire(key, Duration.ofSeconds(policy.windowSeconds()));
			}

			long resetSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
			if (resetSeconds < 0) {
				resetSeconds = policy.windowSeconds();
				redisTemplate.expire(key, Duration.ofSeconds(policy.windowSeconds()));
			}

			long remaining = Math.max(0, policy.limit() - count);
			return new RateLimitResult(count <= policy.limit(), policy.limit(), remaining, resetSeconds, key);
		} catch (RuntimeException exception) {
			log.warn("Rate limit Redis check failed. key={}, policy={}. Fail-open is applied.", key, policy.name(), exception);
			return RateLimitResult.failOpen(key, policy);
		}
	}
}
