package com.classmate.common.ratelimit;

import com.classmate.common.exception.ErrorCode;
import com.classmate.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RateLimitExceededHandler {

	private final ObjectMapper objectMapper;

	public RateLimitExceededHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void handle(HttpServletResponse response, RateLimitResult result) throws IOException {
		addHeaders(response, result);
		response.setStatus(ErrorCode.RATE_LIMIT_EXCEEDED.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), ApiResponse.fail(ErrorCode.RATE_LIMIT_EXCEEDED));
	}

	public void addHeaders(HttpServletResponse response, RateLimitResult result) {
		response.setHeader("X-RateLimit-Limit", Long.toString(result.limit()));
		response.setHeader("X-RateLimit-Remaining", Long.toString(result.remaining()));
		response.setHeader("X-RateLimit-Reset", Long.toString(result.resetSeconds()));
		response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(result.resetSeconds()));
	}
}
