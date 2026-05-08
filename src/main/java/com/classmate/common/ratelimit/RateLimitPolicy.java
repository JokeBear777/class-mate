package com.classmate.common.ratelimit;

public record RateLimitPolicy(
		String name,
		long limit,
		long windowSeconds,
		String keyPrefix
) {
}
