package com.classmate.common.filter;

import com.classmate.common.ratelimit.RateLimitExceededHandler;
import com.classmate.common.ratelimit.RateLimitKeyResolver;
import com.classmate.common.ratelimit.RateLimitPolicy;
import com.classmate.common.ratelimit.RateLimitPolicyResolver;
import com.classmate.common.ratelimit.RateLimitProperties;
import com.classmate.common.ratelimit.RateLimitResult;
import com.classmate.common.ratelimit.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RedisRateLimitFilter extends OncePerRequestFilter {

	private final RateLimitProperties properties;
	private final RateLimitPolicyResolver policyResolver;
	private final RateLimitKeyResolver keyResolver;
	private final RateLimitService rateLimitService;
	private final RateLimitExceededHandler exceededHandler;

	public RedisRateLimitFilter(
			RateLimitProperties properties,
			RateLimitPolicyResolver policyResolver,
			RateLimitKeyResolver keyResolver,
			RateLimitService rateLimitService,
			RateLimitExceededHandler exceededHandler
	) {
		this.properties = properties;
		this.policyResolver = policyResolver;
		this.keyResolver = keyResolver;
		this.rateLimitService = rateLimitService;
		this.exceededHandler = exceededHandler;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		if (!properties.isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}

		RateLimitPolicy policy = policyResolver.resolve(request);
		if (policy == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String key = keyResolver.resolveKey(request, policy);
		RateLimitResult result = rateLimitService.check(key, policy);
		if (!result.allowed()) {
			exceededHandler.handle(response, result);
			return;
		}

		exceededHandler.addHeaders(response, result);
		filterChain.doFilter(request, response);
	}
}
