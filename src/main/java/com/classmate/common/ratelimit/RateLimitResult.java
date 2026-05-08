package com.classmate.common.ratelimit;

public record RateLimitResult(
		boolean allowed,
		long limit,
		long remaining,
		long resetSeconds,
		String key
) {

	public static RateLimitResult failOpen(String key, RateLimitPolicy policy) {
		return new RateLimitResult(true, policy.limit(), policy.limit(), 0, key);
	}
}
