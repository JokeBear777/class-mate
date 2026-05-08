package com.classmate.common.ratelimit;

import com.classmate.common.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RateLimitKeyResolver {

	public String resolveKey(HttpServletRequest request, RateLimitPolicy policy) {
		String subject = resolveSubject(request);
		return switch (policy.name()) {
			case "question" -> policy.keyPrefix() + ":session:" + pathSegment(request, 4) + ":" + subject;
			case "feedback" -> policy.keyPrefix() + ":session:" + pathSegment(request, 4) + ":" + subject;
			case "post" -> policy.keyPrefix() + ":lecture:" + pathSegment(request, 4) + ":" + subject;
			case "team_recruit" -> policy.keyPrefix() + ":lecture:" + pathSegment(request, 4) + ":" + subject;
			case "dashboard" -> policy.keyPrefix() + ":session:" + pathSegment(request, 4) + ":" + subject;
			default -> policy.keyPrefix() + ":" + subject;
		};
	}

	private String resolveSubject(HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null
				&& authentication.isAuthenticated()
				&& authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
			return "user:" + userDetails.getUserId();
		}
		return "ip:" + sanitize(resolveClientIp(request));
	}

	private String resolveClientIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(forwardedFor)) {
			return forwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	private String pathSegment(HttpServletRequest request, int oneBasedIndex) {
		String[] segments = request.getRequestURI().replaceAll("^/+", "").split("/");
		int index = oneBasedIndex - 1;
		if (index < 0 || index >= segments.length) {
			return "unknown";
		}
		return sanitize(segments[index]);
	}

	private String sanitize(String value) {
		return value == null ? "unknown" : value.replaceAll("[^A-Za-z0-9._-]", "_");
	}
}
